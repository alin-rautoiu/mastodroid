package eu.theinvaded.mastondroid.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.FragmentMainBinding;
import eu.theinvaded.mastondroid.ui.activity.SearchActivity;
import eu.theinvaded.mastondroid.ui.adapter.FragmentPager;
import eu.theinvaded.mastondroid.utils.Constants;

/**
 * Created by alin on 10.12.2016.
 */

public class FragmentMain extends FragmentBase {

    private FragmentMainBinding dataBinding;

    public static FragmentMain getInstance() {
        return new FragmentMain();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savendInst) {
        dataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_main, container, false);
        View rootView = dataBinding.getRoot();
        settingsToolbar(rootView);

        if (dataBinding.viewpager != null) {
            setUpViewPager(dataBinding.viewpager);
            dataBinding.tabs.setupWithViewPager(dataBinding.viewpager);
            dataBinding.searchSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    startActivity(SearchActivity.getStartIntent(getContext(), query));
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }


        return rootView;
    }

    private void setUpViewPager(ViewPager viewpager) {
        FragmentPager fragmentPager = new FragmentPager(getChildFragmentManager());
        fragmentPager.addFragment(FragmentTimeline.getInstance(Constants.HOME), "Home");
        fragmentPager.addFragment(FragmentTimeline.getInstance(Constants.NOTIFICATIONS), "Notifications");
        fragmentPager.addFragment(FragmentTimeline.getInstance(Constants.PUBLIC), "Public");

        viewpager.setAdapter(fragmentPager);
    }
}
