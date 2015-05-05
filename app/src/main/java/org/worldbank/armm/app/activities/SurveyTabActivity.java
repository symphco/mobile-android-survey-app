package org.worldbank.armm.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.fragments.SurveyTabFragment;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import org.worldbank.armm.app.models.User;
import org.worldbank.armm.app.tasks.LoadSubmissions;
import org.worldbank.armm.app.utils.Prefs;
import ca.dalezak.androidbase.activities.BaseTabActivity;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Log;

public class SurveyTabActivity extends BaseTabActivity<SurveyTabFragment> {

    public SurveyTabActivity() {
        super(SurveyTabFragment.class, R.menu.menu_survey_tab, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadSubmissions(this).execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK || keyCode==KeyEvent.KEYCODE_HOME) {
            Log.i(this, "KEYCODE_HOME");
            new Alert(this, "Are you sure you want to exit?"){
                @Override
                public void ok() {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }.showOkCancel(R.string.exit, R.string.cancel);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.i(this, "onBackPressed");
        new Alert(this, "Are you sure you want to exit?"){
            @Override
            public void ok() {
                SurveyTabActivity.super.onBackPressed();
            }
        }.showOkCancel(R.string.exit, R.string.cancel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.action_logout) {
            new Alert(this, R.string.confirm_logout, R.string.logout_description) {
                @Override
                public void ok() {
                    new LogoutTask().execute();
                }
            }.showOkCancel(R.string.logout, R.string.cancel);
        }
        return super.onOptionsItemSelected(item);
    }

    private class LogoutTask extends AsyncTask<Void, Void, Intent> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading(R.string.logging_out_);
        }

        @Override
        protected Intent doInBackground(Void... voids) {
            Prefs.clear();
            for (Answer answer : Answer.all()) {
                answer.delete();
            }
            for (Submission submission : Submission.all()) {
                submission.delete();
            }
            for (Question question : Question.all()) {
                question.delete();
            }
            for (Survey survey : Survey.all()) {
                survey.delete();
            }
            for (User user : User.all()) {
                user.delete();
            }
            Intent intent = new Intent(SurveyTabActivity.this, LoginTabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return intent;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            super.onPostExecute(intent);
            hideLoading();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}