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

public class SignupUser extends HttpPostTask<User> {

    private String name;
    private String email;
    private String password;

    public SignupUser(Context context, String name, String email, String password) {
        super(context, Prefs.getServer(), "/api/v1/user/register", R.string.signing_up_);
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    protected void onPrepareRequest() {
        addParameter("username", name);
        addParameter("email", email);
        addParameter("password", password);
    }

    @Override
    protected User onHandleResponse(JSONObject json) throws JSONException {
        Log.i(this, "onHandleResponse %s", json);
        String sessionID = json.optString("sessid");
        String sessionName = json.optString("session_name");
        if (Strings.isNullOrEmpty(sessionID) || Strings.isNullOrEmpty(sessionName)) {
            Prefs.setCookie(null);
        }
        else {
            Prefs.setCookie(String.format("%s=%s", sessionID, sessionName));
        }
        String uid = json.getString(User.Columns.UID);
        User user = User.find(uid);
        if (user == null) {
            user = new User();
            user.uid = uid;
        }
        user.uri = json.getString(User.Columns.URI);
        user.save();
        return user;
    }
}