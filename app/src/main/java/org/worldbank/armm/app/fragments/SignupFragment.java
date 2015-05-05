package org.worldbank.armm.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.EditText;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.activities.SurveyTabActivity;
import org.worldbank.armm.app.models.User;
import org.worldbank.armm.app.tasks.SignupUser;
import org.worldbank.armm.app.utils.Prefs;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.fragments.BaseFragment;
import ca.dalezak.androidbase.tasks.BaseTask;
import ca.dalezak.androidbase.tasks.HttpTask;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;

public class SignupFragment extends BaseFragment {

    @Control("edit_username")
    public EditText editUsername;

    @Control("edit_email")
    public EditText editEmail;

    @Control("edit_password")
    public EditText editPassword;

    public SignupFragment() {
        super(R.layout.fragment_signup, R.menu.menu_signup);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        scaleDrawables(editUsername, 0.9);
        scaleDrawables(editEmail, 0.9);
        scaleDrawables(editPassword, 0.9);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_signup) {
            if (editUsername.getText().toString().trim().length() == 0) {
                new Alert(getActivity(), R.string.name_required, R.string.name_required_description) {
                    @Override
                    public void ok() {
                        editUsername.requestFocus();
                    }
                }.showOk(R.string.ok);
            } else if (editEmail.getText().toString().trim().length() == 0) {
                new Alert(getActivity(), R.string.email_required, R.string.email_required_description) {
                    @Override
                    public void ok() {
                        editEmail.requestFocus();
                    }
                }.showOk(R.string.ok);
            } else if (editPassword.getText().toString().trim().length() == 0) {
                new Alert(getActivity(), R.string.password_required, R.string.password_required_description) {
                    @Override
                    public void ok() {
                        editPassword.requestFocus();
                    }
                }.showOk(R.string.ok);
            } else {
                Prefs.setName(null);
                Prefs.setEmail(null);
                Prefs.setPassword(null);
                SignupUser signupUser = new SignupUser(
                        getActivity(),
                        editUsername.getText().toString(),
                        editEmail.getText().toString(),
                        editPassword.getText().toString());
                signupUser.register(new RegisterUserCallback());
                signupUser.execute();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startActivitySurveyList() {
        Intent intent = new Intent(getActivity(), SurveyTabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private class RegisterUserCallback implements BaseTask.Callback<HttpTask, User> {

        @Override
        public void onTaskStarted(HttpTask task) {
            Log.i(this, "onTaskStarted %s", task);
            showLoading(R.string.signing_up_);
        }

        @Override
        public void onTaskCancelled(HttpTask task) {
            Log.i(this, "onTaskCancelled %s", task);
        }

        @Override
        public void onTaskProgress(HttpTask task, User user, int total, int progress) {
            Log.i(this, "onTaskProgress %s %d/%d", task, progress, total);
        }

        @Override
        public void onTaskFinished(HttpTask task) {
            Log.w(this, "onTaskFinished %s", task);
            Prefs.setName(editUsername.getText().toString());
            Prefs.setEmail(editEmail.getText().toString());
            Prefs.setPassword(editPassword.getText().toString());
            startActivitySurveyList();
            hideLoading();
        }

        @Override
        public void onTaskFailed(HttpTask task, Exception exception) {
            Log.w(this, "onTaskFailed", exception);
            Prefs.setName(null);
            Prefs.setEmail(null);
            Prefs.setPassword(null);
            hideLoading();
            if (exception == null || Strings.isNullOrEmpty(exception.getMessage())) {
                new Alert(getActivity(), R.string.signup_failed, R.string.signup_failed_description).showOk(R.string.ok);
            }
            else {
                String message = exception.getMessage();
                message = message.replace("Value [\"", "");
                message = message.replace("\"] of type org.json.JSONArray cannot be converted to JSONObject", "");
                message = Html.fromHtml(message).toString();
                new Alert(getActivity(), R.string.signup_failed, message).showOk(R.string.ok);
            }
        }
    }
}