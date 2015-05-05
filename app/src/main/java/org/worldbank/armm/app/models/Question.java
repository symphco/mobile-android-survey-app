package org.worldbank.armm.app.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import ca.dalezak.androidbase.utils.Strings;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Table(name = "Questions")
public class Question extends Model {

    public class Columns extends Model.Columns {
        public static final String NID = "nid";
        public static final String CID = "cid";
        public static final String PID = "pid";
        public static final String KEY = "form_key";
        public static final String TYPE = "type";
        public static final String NAME = "name";
        public static final String VALUE = "value";
        public static final String WEIGHT = "weight";
        public static final String REQUIRED = "required";

        public static final String POSITION = "position";
        public static final String PLACEHOLDER = "placeholder";
        public static final String SURVEY = "survey";
        public static final String DESCRIPTION = "description";
        public static final String MIN = "min";
        public static final String MAX = "max";

        public static final String ITEMS = "items";
        public static final String OPTIONS = "options";
        public static final String QUESTIONS = "questions";

        public static final String PREFIX = "field_prefix";
        public static final String SUFFIX = "field_suffix";
    }

    @Column(name = Columns.NAME)
    public String name;

    @Column(name = Columns.TYPE)
    public String type;

    @Column(name = Columns.KEY)
    public String key;

    @Column(name = Columns.NID)
    public Integer nid;

    @Column(name = Columns.CID)
    public Integer cid;

    @Column(name = Columns.PID)
    public Integer pid;

    @Column(name = Columns.POSITION)
    public Integer position;

    @Column(name = Columns.PLACEHOLDER)
    public String placeholder;

    @Column(name = Columns.REQUIRED)
    public Boolean required;

    @Column(name = Columns.DESCRIPTION)
    public String description;

    @Column(name = Columns.MIN)
    public Integer min;

    @Column(name = Columns.MAX)
    public Integer max;

    @Column(name = Columns.VALUE)
    public String value;

    @Column(name = Columns.ITEMS)
    public String items;

    @Column(name = Columns.OPTIONS)
    public String options;

    @Column(name = Columns.QUESTIONS)
    public String questions;

    @Column(name = Columns.WEIGHT)
    public Double weight;

    @Column(name = Columns.SURVEY)
    public Survey survey;

    @Column(name = Columns.PREFIX)
    public String prefix;

    @Column(name = Columns.SUFFIX)
    public String suffix;

    @Override
    public boolean matches(String text) {
        return Strings.anyWordStartsWith(text, name);
    }

    public static List<Question> all() {
        return all(Question.class);
    }

    public List<Question> children() {
        return new Select()
                .from(Question.class)
                .where(String.format("%s = ? AND %s = ?", Columns.SURVEY, Columns.PID), survey.getId(), cid)
                .execute();
    }

    //"yes|yes\r\nno|no\r\n"
    //"1|male\r\n2|female\r\n"
    public HashMap<String, String> items() {
        HashMap<String, String> map = new LinkedHashMap<>();
        if (!Strings.isNullOrEmpty(items)) {
            for (String item : items.split("[\\r\\n]+")) {
                String[] components = item.split("\\|");
                String key = components[0];
                String value = components[1];
                map.put(key, value);
            }
        }
        return map;
    }

    //1|Question 1\r\n2|Question 2\r\n3|Question 3\r\n4|Question 4
    public HashMap<String, String> questions() {
        HashMap<String, String> map = new LinkedHashMap<>();
        if (!Strings.isNullOrEmpty(questions)) {
            for (String item : questions.split("[\\r\\n]+")) {
                String[] components = item.split("\\|");
                String key = components[0];
                String value = components[1];
                map.put(key, value);
            }
        }
        return map;
    }

    //1|Option1\r\n2|Option2\r\n3|Option3\r\n4|Option4
    public HashMap<String, String> options() {
        HashMap<String, String> map = new LinkedHashMap<>();
        if (!Strings.isNullOrEmpty(options)) {
            for (String item : options.split("[\\r\\n]+")) {
                String[] components = item.split("\\|");
                String key = components[0];
                String value = components[1];
                map.put(key, value);
            }
        }
        return map;
    }

    public static Question find(Integer nid, Integer cid) {
        if (nid != null && cid != null) {
            String query = String.format("%s = %s AND %s = ?", Columns.NID, nid.toString(), Columns.CID);
            return find(Question.class, query, cid.toString());
        }
        return null;
    }

}