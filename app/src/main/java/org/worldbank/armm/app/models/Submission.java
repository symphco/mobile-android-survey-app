package org.worldbank.armm.app.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

@Table(name = "Submissions")
public class Submission extends Model {

    public class Columns extends Model.Columns {
        public static final String UUID = "uuid";
        public static final String URI = "uri";
        public static final String NID = "nid";
        public static final String SID = "sid";
        public static final String TITLE = "title";
        public static final String USER = "user";
        public static final String SURVEY = "survey";
        public static final String CREATED = "created";
        public static final String CHANGED = "changed";
        public static final String UPLOADED = "uploaded";
        public static final String COMPLETED = "completed";
        public static final String REQUIRED_QUESTIONS = "required_questions";
        public static final String REQUIRED_ANSWERS = "required_answers";
    }

    @Column(name = Columns.UUID, unique = true, index = true)
    public String uuid;

    @Column(name = Columns.URI)
    public String uri;

    @Column(name = Columns.NID)
    public Integer nid;

    @Column(name = Columns.SID)
    public Integer sid;

    @Column(name = Columns.TITLE)
    public String title;

    @Column(name = Columns.USER)
    public User user;

    @Column(name = Columns.SURVEY)
    public Survey survey;

    @Column(name = Columns.CREATED)
    public Date created;

    @Column(name = Columns.CHANGED)
    public Date changed;

    @Column(name = Columns.UPLOADED)
    public Date uploaded;

    @Column(name = Columns.COMPLETED)
    public Date completed;

    @Column(name = Columns.REQUIRED_QUESTIONS)
    public Integer requiredQuestions;

    @Column(name = Columns.REQUIRED_ANSWERS)
    public Integer requiredAnswers;

    public static List<Submission> all() {
        return all(Submission.class, Survey.Columns.CHANGED, false);
    }

    public static List<Submission> completed() {
        return where(Submission.class, Survey.Columns.CHANGED, false, String.format("%s IS NOT NULL", Columns.COMPLETED));
    }

    public static List<Submission> incomplete() {
        return where(Submission.class, Survey.Columns.CHANGED, false, String.format("%s IS NULL", Columns.COMPLETED));
    }

    public static List<Submission> pending() {
        return where(Submission.class, Survey.Columns.CHANGED, false, String.format("%s IS NULL AND %s IS NOT NULL", Columns.UPLOADED, Columns.COMPLETED));
    }

    public List<Answer> answers() {
        return all(Answer.class, this, Answer.Columns.WEIGHT, true);
    }

    public Integer answerCount() {
        return count(Answer.class, String.format("%s = ? AND %s IS NOT NULL", Answer.Columns.SUBMISSION, Answer.Columns.VALUE), getId());
    }

    public Answer answerForQuestion(Question question) {
        Answer answer = Answer.find(this, question);
        if (answer == null && question != null) {
            answer = new Answer();
            answer.submission = this;
            answer.cid = question.cid;
            answer.question = question;
            answer.name = question.name;
            answer.key = question.key;
            answer.weight = question.weight;
            answer.save();
        }
        return answer;
    }

    public static Submission find(String uuid) {
        if (uuid != null) {
            return find(Submission.class, String.format("%s = ?", Columns.UUID), uuid);
        }
        return null;
    }

    @Override
    public boolean matches(String text) {
        return true;
    }

    public boolean hasAnswers() {
        for (Answer answer : answers()) {
            if (answer.hasValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCompleted() {
        for (Question question : survey.allQuestions()) {
            if (question.required) {
                Answer answer = answerForQuestion(question);
                if (answer == null) {
                    return false;
                }
                else if (!answer.hasValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public int updateQuestions() {
        int requiredQuestions = 0;
        for (Question question : survey.allQuestions()) {
            if (question.required) {
                requiredQuestions++;
            }
        }
        this.requiredQuestions = requiredQuestions;
        this.save();
        return requiredQuestions;
    }

    public void updateChanged() {
        changed = new Date();
    }

    public int updateAnswers() {
        int requiredAnswers = 0;
        for (Answer answer : answers()) {
            if (answer.question.required && answer.hasValue()) {
                requiredAnswers++;
            }
        }
        this.requiredAnswers = requiredAnswers;
        this.save();
        return requiredAnswers;
    }

    public boolean updateCompleted() {
        if (this.completed == null) {
            boolean completed = true;
            for (Question question : survey.allQuestions()) {
                if (question.required) {
                    Answer answer = Answer.find(this, question);
                    if (answer == null) {
                        completed = false;
                    }
                    else if (!answer.hasValue()) {
                        completed = false;
                    }
                }
            }
            if (completed) {
                this.completed = new Date();
            }
            else {
                this.completed = null;
            }
            this.save();
            return completed;
        }
        return false;
    }

    public void deleteAnswers() {
        for (Answer answer : answers()) {
            answer.delete();
        }
    }

    public int requiredAnswers() {
        return requiredAnswers != null ? requiredAnswers : 0;
    }

    public int requiredQuestions() {
        return requiredQuestions != null ? requiredQuestions : 0;
    }
}