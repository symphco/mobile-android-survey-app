package org.worldbank.armm.app.tasks;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.User;
import org.worldbank.armm.app.utils.Prefs;
import ca.dalezak.androidbase.tasks.HttpPostTask;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;

public class LoginUser extends HttpPostTask<User> {

    private static final String USER = "user";
    private static final String TOKEN = "token";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SESSION_ID = "sessid";
    private static final String SESSION_NAME = "session_name";

    private String username;
    private String password;

    public LoginUser(Context context, String username, String password) {
        super(context, Prefs.getServer(), "/api/v1/user/login", R.string.logging_in_);
        this.username = username;
        this.password = password;
    }

    @Override
    protected void onPrepareRequest() {
        addParameter(USERNAME, username);
        addParameter(PASSWORD, password);
    }

    @Override
    protected User onHandleResponse(JSONObject json) throws JSONException {
        Log.i(this, "JSON %s", json);
        String sessionID = json.optString(SESSION_ID);
        String sessionName = json.optString(SESSION_NAME);
        String token = json.optString(TOKEN);
        if (Strings.isNullOrEmpty(sessionID) || Strings.isNullOrEmpty(sessionName)) {
            Prefs.setCookie(null);
        }
        else {
            Prefs.setCookie(String.format("%s=%s", sessionName, sessionID));
        }
        Prefs.setToken(token);
        json = json.getJSONObject(USER);
        String uid = json.getString(User.Columns.UID);
        User user = User.find(uid);
        if (user == null) {
            user = new User();
            user.uid = uid;
        }
        user.name = json.getString(User.Columns.NAME);
        user.uid = json.getString(User.Columns.UID);
        user.email = json.getString(User.Columns.EMAIL);
        user.save();
        return user;
    }
}