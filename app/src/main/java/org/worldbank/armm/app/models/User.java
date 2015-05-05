package org.worldbank.armm.app.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Users")
public class User extends Model {

    public class Columns extends Model.Columns {
        public static final String URI = "uri";
        public static final String UID = "uid";
        public static final String NAME = "name";
        public static final String EMAIL = "mail";
        public static final String PASSWORD = "password";
    }

    @Column(name = Columns.UID)
    public String uid;

    @Column(name = Columns.URI)
    public String uri;

    @Column(name = Columns.NAME)
    public String name;

    @Column(name = Columns.EMAIL)
    public String email;

    @Column(name = Columns.PASSWORD)
    public String password;

    public static List<User> all() {
        return all(User.class);
    }

    public List<Submission> submissions() {
        return all(Submission.class, this, Submission.Columns.CREATED, true);
    }

    public static User find(String uid) {
        if (uid != null) {
            return find(User.class, String.format("%s = ?", Columns.UID), uid);
        }
        return null;
    }

    @Override
    public boolean matches(String text) {
        return true;
    }
}