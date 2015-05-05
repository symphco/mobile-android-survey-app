package org.worldbank.armm.app.activities;

import android.content.Intent;

import org.worldbank.armm.app.fragments.QuestionTabFragment;
import ca.dalezak.androidbase.activities.BaseActivity;
import ca.dalezak.androidbase.utils.Log;

public class QuestionTabActivity extends BaseActivity<QuestionTabFragment> {

    public QuestionTabActivity() {
        super(QuestionTabFragment.class, true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getFragment().onActivityResult(requestCode, resultCode, data);
    }
}