package org.worldbank.armm.app.fragments;

import android.text.Html;
import android.widget.TextView;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.annotations.Type;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;

@Type(MarkupFragment.TYPE)
public class MarkupFragment extends WidgetFragment {

    public static final String TYPE = "markup";

    @Control("label_markup")
    public TextView labelMarkup;

    public MarkupFragment() {
        super(R.layout.fragment_markup, R.menu.menu_markup);
    }

    @Override
    public boolean load(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "load %d %s %s", question.cid, question.name, answer.value);
        if (Strings.isNullOrEmpty(question.value)) {
            labelMarkup.setText(null);
        }
        else {
            Log.i(this, "HTML %s", question.value);
            labelMarkup.setText(Html.fromHtml(question.value));
        }
        return true;
    }

    @Override
    public boolean save(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "save %d %s %s", question.cid, question.name, answer.value);
        return true;
    }
}