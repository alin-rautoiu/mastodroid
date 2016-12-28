package eu.theinvaded.mastondroid.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.FragmentMainBinding;
import eu.theinvaded.mastondroid.ui.adapter.FragmentPager;
import eu.theinvaded.mastondroid.viewmodel.MainViewModel;

/**
 * Created by alin on 10.12.2016.
 */

public class FragmentMain extends FragmentBase {

    public final static int HOME = 1;
    public final static int NOTIFICATIONS = 2;
    public final static int PUBLIC = 3;

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
            setUpViewPage(dataBinding.viewpager);
            dataBinding.tabs.setupWithViewPager(dataBinding.viewpager);
            dataBinding.setViewModel(new MainViewModel(getContext()));
            dataBinding.toolbar.setTitle("");
        }

        return rootView;
    }

    private void setUpViewPage(ViewPager viewpager) {
        FragmentPager fragmentPager = new FragmentPager(getChildFragmentManager());
        fragmentPager.addFragment(FragmentTimeline.getInstance(HOME), "Home");
        fragmentPager.addFragment(FragmentTimeline.getInstance(NOTIFICATIONS), "Notifications");
        fragmentPager.addFragment(FragmentTimeline.getInstance(PUBLIC), "Public");

        dataBinding.viewpager.setAdapter(fragmentPager);
    }
}
