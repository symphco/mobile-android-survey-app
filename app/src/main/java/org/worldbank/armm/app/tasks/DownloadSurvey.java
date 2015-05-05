package org.worldbank.armm.app.tasks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Survey;
import org.worldbank.armm.app.utils.Prefs;
import ca.dalezak.androidbase.tasks.HttpGetTask;
import ca.dalezak.androidbase.utils.Dates;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class DownloadSurvey extends HttpGetTask<Survey> {

    private static final List<String> EXCLUDED = Arrays.asList("hidden", "pagebreak");
    private static final String BODY = "body";
    private static final String COMPONENTS = "components";
    private static final String EXTRA = "extra";
    private static final String NODE = "node";
    private static final String BODY_SUMMARY = "body_summary";
    private static final String BODY_VALUE = "body_value";
    private static final String SINCE = "since";
    private static final String COOKIE = "Cookie";

    private URI uri;

    public DownloadSurvey(Context context, Survey survey) {
        this(context, survey, false);
    }

    public DownloadSurvey(Context context, Survey survey, boolean loading) {
        super(context, Prefs.getServer(), String.format("api/v1/surveys/%s", survey.nid), R.string.downloading_survey_, loading);
        this.uri = getURI();
    }

    @Override
    protected void onPrepareRequest() {
        if (Prefs.hasSince(uri)) {
            Date date = Prefs.getSince(uri);
            addParameter(SINCE, Dates.toEpochString(date));
        }
        addHeader(COOKIE, Prefs.getCookie());
        setUsername(Prefs.getUsername());
        setPassword(Prefs.getPassword());
    }

    @Override
    protected Survey onHandleResponse(JSONObject json) throws JSONException {
        Log.i(this, "JSON %s", json);
        JSONObject node = json.getJSONObject(NODE);
        Integer nid = node.getInt(Survey.Columns.NID);
        Survey survey = Survey.find(nid);
        if (survey == null) {
            survey = new Survey();
            survey.nid = nid;
        }
        JSONObject body = json.optJSONObject(BODY);
        if (body != null) {
            survey.description = body.optString(BODY_SUMMARY);
            if (Strings.isNullOrEmpty(survey.description)) {
                survey.description = body.optString(BODY_VALUE);
                survey.description = Strings.stripHtml(survey.description);
                survey.description = survey.description.replaceAll("\\s+", " ");
            }
        }
        HashMap<Integer, Double> weights = new HashMap<>();
        TreeMap<Double, Question> questions = new TreeMap<>();
        JSONArray components = json.getJSONArray(COMPONENTS);
        for (int i = 0; i < components.length(); i++) {
            JSONObject component = components.getJSONObject(i);
            String type = component.optString(Question.Columns.TYPE);
            if (!EXCLUDED.contains(type)) {
                Integer qid = component.getInt(Question.Columns.NID);
                Integer cid = component.getInt(Question.Columns.CID);
                Question question = Question.find(qid, cid);
                if (question == null) {
                    question = new Question();
                }
                question.nid = qid;
                question.cid = cid;
                question.type = type;
                question.survey = survey;
                question.pid = component.getInt(Question.Columns.PID);
                question.weight = component.getDouble(Question.Columns.WEIGHT);
                question.key = component.getString(Question.Columns.KEY);
                question.name = component.getString(Question.Columns.NAME);
                question.value = component.optString(Question.Columns.VALUE);
                question.required = component.optInt(Question.Columns.REQUIRED, 0) == 1;
                if (component.has(EXTRA)) {
                    JSONObject extra = component.getJSONObject(EXTRA);
                    if (extra != null) {
                        question.min = extra.optInt(Question.Columns.MIN, -1);
                        question.max = extra.optInt(Question.Columns.MAX, -1);
                        question.prefix = extra.optString(Question.Columns.PREFIX);
                        question.suffix = extra.optString(Question.Columns.SUFFIX);
                        question.description = extra.optString(Question.Columns.DESCRIPTION);
                        question.placeholder = extra.optString(Question.Columns.PLACEHOLDER);
                        question.items = extra.optString(Question.Columns.ITEMS);
                        question.options = extra.optString(Question.Columns.OPTIONS);
                        question.questions = extra.optString(Question.Columns.QUESTIONS);
                    }
                }
                question.save();
                questions.put(question.weight, question);
                weights.put(question.cid, question.weight);
            }
        }
        int position = 0;
        for (Question question : questions.values()) {
            if (question.pid == 0)  {
                question.position = position;
                position = position + 1;
                Log.i(this, "Question %s %s %s", question.position, question.weight, question.name);
            }
            else {
                question.position = -1;
                try {
                    double parentWeight = weights.get(question.pid);
                    double childWeight = question.weight;
                    String decimal = String.format("%d.%d", Math.round(parentWeight), Math.round(childWeight));
                    question.weight = Double.parseDouble(decimal);
                }
                catch (NumberFormatException ex) {
                    Log.w(this, "NumberFormatException", ex);
                }
                Log.i(this, "Sub Question %s %s", question.weight, question.name);
            }
            question.save();
        }
        survey.save();
        Prefs.setSince(uri, new Date());
        return survey;
    }

}