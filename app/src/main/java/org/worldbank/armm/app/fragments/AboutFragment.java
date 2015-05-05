package org.worldbank.armm.app.fragments;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.worldbank.armm.app.Application;
import org.worldbank.armm.app.R;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.fragments.BaseFragment;

public class AboutFragment extends BaseFragment {

    @Control("app_version")
    public TextView labelVersion;

    public AboutFragment() {
        super(R.layout.fragment_about, R.menu.menu_about);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Application application = (Application)getActivity().getApplication();
        labelVersion.setText(String.format("%s %s", getString(R.string.app_name), application.getVersion()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_done) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
