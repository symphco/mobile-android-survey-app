package org.worldbank.armm.app.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;
import ca.dalezak.androidbase.utils.JSON;
import ca.dalezak.androidbase.utils.Strings;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Table(name = "Answers")
public class Answer extends Model {

    public class Columns extends Model.Columns {
        public static final String SUBMISSION = "submission";
        public static final String QUESTION = "question";
        public static final String VALUE = "value";
        public static final String NAME = "name";
        public static final String KEY = "key";
        public static final String CID = "cid";
        public static final String WEIGHT = "weight";
        public static final String URI = "uri";
    }

    @Column(name = Columns.SUBMISSION)
    public Submission submission;

    @Column(name = Columns.QUESTION)
    public Question question;

    @Column(name = Columns.KEY)
    public String key;

    @Column(name = Columns.NAME)
    public String name;

    @Column(name = Columns.VALUE)
    public String value;

    @Column(name = Columns.CID)
    public Integer cid;

    @Column(name = Columns.WEIGHT)
    public Double weight;

    @Column(name = Columns.URI)
    public String uri;

    @Override
    public boolean matches(String text) {
        return true;
    }

    public static List<Answer> all() {
        return all(Answer.class);
    }

    public Map<String, Object> valueMap() {
        if (!Strings.isNullOrEmpty(value)) {
            try {
                JSONObject jsonObject = new JSONObject(value);
                return JSON.jsonToMap(jsonObject);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    public static Answer find(Submission submission, Question question) {
        if (submission != null && question != null) {
            return new Select()
                    .from(Answer.class)
                    .where(String.format("%s = ? AND %s = ?", Answer.Columns.SUBMISSION, Answer.Columns.CID), submission.getId(), question.cid)
                    .executeSingle();
        }
        return null;
    }

    public boolean hasValue() {
        return !Strings.isNullOrEmpty(value);
    }

    public boolean hasFile() {
        if (!Strings.isNullOrEmpty(value)) {
            File file = new File(value);
            return file.exists();
        }
        return false;
    }

    public File getFile() {
        if (!Strings.isNullOrEmpty(value)) {
            File file = new File(value);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
}