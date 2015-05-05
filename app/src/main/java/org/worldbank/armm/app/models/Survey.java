package org.worldbank.armm.app.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import ca.dalezak.androidbase.utils.Strings;

import java.util.Date;
import java.util.List;

@Table(name = "Surveys")
public class Survey extends Model {

    public class Columns extends Model.Columns {
        public static final String URI = "uri";
        public static final String NID = "nid";
        public static final String TYPE = "type";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String CREATED = "created";
        public static final String CHANGED = "changed";
    }

    @Column(name = Columns.NID, unique = true, index = true)
    public Integer nid;

    @Column(name = Columns.URI)
    public String uri;

    @Column(name = Columns.TYPE)
    public String type;

    @Column(name = Columns.TITLE)
    public String title;

    @Column(name = Columns.DESCRIPTION)
    public String description;

    @Column(name = Columns.CREATED)
    public Date created;

    @Column(name = Columns.CHANGED)
    public Date changed;

    public List<Question> allQuestions() {
        return all(Question.class, this, Question.Columns.WEIGHT, true);
    }

    public List<Question> parentQuestions() {
        return new Select()
                .from(Question.class)
                .where(String.format("%s = ? AND %s = 0 AND %s > -1",
                        Question.Columns.SURVEY, Question.Columns.PID, Question.Columns.POSITION), getId())
                .orderBy(Question.Columns.POSITION)
                .execute();
    }

    public Integer allQuestionCount() {
        return count(Question.class, String.format("%s = ?", Question.Columns.SURVEY), getId());
    }

    public Integer parentQuestionCount() {
        return count(Question.class, String.format("%s = ? AND %s = 0 AND %s > -1",
                Question.Columns.SURVEY, Question.Columns.PID, Question.Columns.POSITION), getId());
    }

    public Question questionByPosition(int position) {
        if (position > -1) {
            return new Select()
                    .from(Question.class)
                    .where(String.format("%s = ? AND %s = ?", Question.Columns.SURVEY, Question.Columns.POSITION), getId(), position)
                    .executeSingle();
        }
        return null;
    }

    @Override
    public boolean matches(String text) {
        return Strings.anyWordStartsWith(text, title, description);
    }

    public static List<Survey> all() {
        return all(Survey.class, Survey.Columns.CHANGED, false);
    }

    public static Integer count() {
        return count(Survey.class);
    }

    public static Survey find(Integer nid) {
        if (nid != null) {
            return find(Survey.class, String.format("%s = ?", Columns.NID), nid.toString());
        }
        return null;
    }

}