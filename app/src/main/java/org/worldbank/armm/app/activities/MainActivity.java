package org.worldbank.armm.app.activities;

import android.content.Intent;

import org.worldbank.armm.app.Application;
import org.worldbank.armm.app.R;
import org.worldbank.armm.app.fragments.MainFragment;
import org.worldbank.armm.app.models.Model;
import org.worldbank.armm.app.utils.Prefs;

import ca.dalezak.androidbase.activities.BaseActivity;
import ca.dalezak.androidbase.utils.Alert;

public class MainActivity extends BaseActivity<MainFragment> {

    public MainActivity() {
        super(MainFragment.class, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Model.allModelsAreValid(this)) {
            new Alert(this,
                    R.string.database_changed,
                    R.string.database_changed_description) {
                @Override
                public void ok() {
                    Application application = (Application)getApplication();
                    application.deleteDatabase();
                    application.restartApplication();
                }
            }.showOk(R.string.restart_application);
        }
        else if (Prefs.hasUsername() && Prefs.hasPassword() && Prefs.hasCookie()) {
            Intent intent = new Intent(this, SurveyTabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, LoginTabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
