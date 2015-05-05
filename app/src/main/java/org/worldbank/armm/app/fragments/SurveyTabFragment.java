package org.worldbank.armm.app.fragments;

import android.content.Intent;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Submission;

import ca.dalezak.androidbase.fragments.BaseFragment;
import ca.dalezak.androidbase.fragments.BaseTabFragment;
import ca.dalezak.androidbase.utils.Log;

public class SurveyTabFragment extends BaseTabFragment<BaseFragment> {

    private Submission submission;

    public SurveyTabFragment() {
        super(R.layout.fragment_tabs);
        addTab(R.string.surveys, SurveyCardsFragment.class);
        addTab(R.string.incomplete, IncompleteCardsFragment.class);
        addTab(R.string.completed, CompletedCardsFragment.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(this, "onActivityResult %d %d %s", requestCode, resultCode, data);
        if (data != null && data.hasExtra(Submission.class.getName())) {
            submission = Submission.find(data.getStringExtra(Submission.class.getName()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (submission != null) {
            if (submission.isCompleted()) {
                Log.i(this, "Submission Completed %s", submission);
                setTabSelected(2, false);
            }
            else {
                Log.i(this, "Submission Incomplete %s", submission);
                setTabSelected(1, false);
            }
        }
    }

    @Override
    protected boolean onTabSelected(int position, BaseFragment fragment) {
        Log.i(this, "onTabSelected %d %s", position, fragment);
        return true;
    }

    @Override
    protected boolean onTabUnselected(int position, BaseFragment fragment) {
        Log.i(this, "onTabUnselected %d %s", position, fragment);
        return true;
    }

}