package org.worldbank.armm.app.fragments;

import android.content.Intent;
import android.text.Html;
import android.view.MenuItem;
import android.widget.EditText;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.activities.SurveyTabActivity;
import org.worldbank.armm.app.models.User;
import org.worldbank.armm.app.tasks.LoginUser;
import org.worldbank.armm.app.utils.Prefs;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.fragments.BaseFragment;
import ca.dalezak.androidbase.tasks.BaseTask;
import ca.dalezak.androidbase.tasks.HttpTask;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Internet;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;

public class LoginFragment extends BaseFragment {

    @Control("edit_username")
    public EditText editUsername;

    @Control("edit_password")
    public EditText editPassword;

    public LoginFragment() {
        super(R.layout.fragment_login, R.menu.menu_login);
    }

    @Override
    public void onResume() {
        super.onResume();
        scaleDrawables(editUsername, 0.9);
        scaleDrawables(editPassword, 0.9);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_login) {
            if (editUsername.getText().toString().trim().length() == 0) {
                new Alert(getActivity(), R.string.username_required, R.string.username_required_description) {
                    @Override
                    public void ok() {
                        editUsername.requestFocus();
                    }
                }.showOk(R.string.ok);
            }
            else if (editPassword.getText().toString().trim().length() == 0) {
                new Alert(getActivity(), R.string.password_required, R.string.password_required_description) {
                    @Override
                    public void ok() {
                        editPassword.requestFocus();
                    }
                }.showOk(R.string.ok);
            }
            else if (!Internet.isAvailable(getActivity())) {
                new Alert(getActivity(),
                        R.string.no_internet_connection,
                        R.string.verify_internet_connection).showOk(R.string.ok);
            }
            else {
                hideKeyboard();
                Prefs.clearCookie();
                LoginUser loginUser = new LoginUser(
                        getActivity(),
                        editUsername.getText().toString(),
                        editPassword.getText().toString());
                loginUser.register(new LoginUserCallback());
                loginUser.execute();
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

    private class LoginUserCallback implements BaseTask.Callback<HttpTask, User> {

        private User user;

        @Override
        public void onTaskStarted(HttpTask task) {
            Log.i(this, "onTaskStarted %s", task);
            showLoading(R.string.logging_in_);
        }

        @Override
        public void onTaskCancelled(HttpTask task) {
            Log.i(this, "onTaskCancelled %s", task);
        }

        @Override
        public void onTaskProgress(HttpTask task, User user, int total, int progress) {
            Log.i(this, "onTaskProgress %s %d/%d", task, progress, total);
            this.user = user;
        }

        @Override
        public void onTaskFinished(HttpTask task) {
            Log.i(this, "onTaskFinished %s", task);
            if (user != null) {
                Prefs.setUsername(editUsername.getText().toString());
                Prefs.setPassword(editPassword.getText().toString());
                startActivitySurveyList();
            }
            else {
                Prefs.clearCookie();
                Prefs.setUsername(null);
                Prefs.setPassword(null);
                new Alert(getActivity(),
                        R.string.invalid_credentials,
                        R.string.invalid_credentials_description) {

                }.showOk(R.string.ok);
            }
            hideLoading();
        }

        @Override
        public void onTaskFailed(HttpTask task, Exception exception) {
            Log.w(this, "onTaskFailed", exception);
            Prefs.clearCookie();
            Prefs.setUsername(null);
            Prefs.setPassword(null);
            hideLoading();
            if (exception == null || Strings.isNullOrEmpty(exception.getMessage())) {
                new Alert(getActivity(), R.string.invalid_credentials, R.string.invalid_credentials_description).showOk(R.string.ok);
            }
            else if (exception.getMessage().contains("Already logged in as")) {
                Log.w(this, exception.getMessage());
                new Alert(getActivity(), R.string.already_logged_in, R.string.already_logged_in_description).showOk(R.string.ok);
            }
            else {
                String message = exception.getMessage();
                message = message.replace("Value [\"", "");
                message = message.replace("\"] of type org.json.JSONArray cannot be converted to JSONObject", "");
                message = Html.fromHtml(message).toString();
                new Alert(getActivity(), R.string.invalid_credentials, message).showOk(R.string.ok);
            }
        }
    }
}