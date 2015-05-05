package org.worldbank.armm.app.activities;

import android.os.Bundle;

import org.worldbank.armm.app.fragments.AboutFragment;
import ca.dalezak.androidbase.activities.BaseActivity;

public class AboutActivity extends BaseActivity<AboutFragment> {

    public AboutActivity() {
        super(AboutFragment.class, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}