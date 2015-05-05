package org.worldbank.armm.app.fragments;

import android.content.Intent;
import android.view.MenuItem;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.activities.AboutActivity;

import ca.dalezak.androidbase.fragments.BaseFragment;
import ca.dalezak.androidbase.fragments.BaseTabFragment;

public class LoginTabFragment extends BaseTabFragment<BaseFragment> {

    public LoginTabFragment() {
        super(R.layout.fragment_tabs, R.menu.menu_login_tab);
        addTab(R.string.login, LoginFragment.class);
        //addTab(R.string.signup, SignupFragment.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean onTabSelected(int position, BaseFragment fragment) {
        return true;
    }

    @Override
    protected boolean onTabUnselected(int position, BaseFragment fragment) {
        return true;
    }

}