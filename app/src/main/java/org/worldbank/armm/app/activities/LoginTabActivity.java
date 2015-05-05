package org.worldbank.armm.app.activities;

import android.os.Bundle;

import org.worldbank.armm.app.fragments.LoginTabFragment;
import ca.dalezak.androidbase.activities.BaseTabActivity;

public class LoginTabActivity extends BaseTabActivity<LoginTabFragment> {

    public LoginTabActivity() {
        super(LoginTabFragment.class, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}