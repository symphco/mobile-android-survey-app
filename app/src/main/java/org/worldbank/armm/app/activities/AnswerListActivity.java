package org.worldbank.armm.app.activities;

import org.worldbank.armm.app.fragments.AnswerListFragment;
import ca.dalezak.androidbase.activities.BaseListActivity;

public class AnswerListActivity extends BaseListActivity<AnswerListFragment> {

    public AnswerListActivity() {
        super(AnswerListFragment.class, true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}