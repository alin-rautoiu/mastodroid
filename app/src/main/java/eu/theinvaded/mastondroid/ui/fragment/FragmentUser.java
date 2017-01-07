package eu.theinvaded.mastondroid.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.FragmentMainBinding;
import eu.theinvaded.mastondroid.databinding.FragmentUserBinding;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.ui.adapter.FragmentPager;
import eu.theinvaded.mastondroid.utils.Constants;
import eu.theinvaded.mastondroid.viewmodel.MainViewModel;
import eu.theinvaded.mastondroid.viewmodel.UserViewModel;
import eu.theinvaded.mastondroid.viewmodel.UserViewModelContract;

/**
 * Created by alin on 10.12.2016.
 */

public class FragmentUser extends FragmentBase implements UserViewModelContract.UserView {

    private FragmentUserBinding dataBinding;
    private MastodonAccount account;

    public static FragmentUser getInstance(MastodonAccount account) {
        FragmentUser fragmentUser = new FragmentUser();

        Bundle args = new Bundle();
        args.putParcelable("USER", account);
        fragmentUser.setArguments(args);

        return fragmentUser;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savendInst) {
        dataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_user, container, false);
        View rootView = dataBinding.getRoot();
        settingsToolbar(rootView);

        account = getArguments().getParcelable("USER");

        if (dataBinding.viewpager != null) {
            setUpViewPage(dataBinding.viewpager);
            dataBinding.tabs.setupWithViewPager(dataBinding.viewpager);
            dataBinding.setViewModel(new UserViewModel(this, account));
        }

        return rootView;
    }

    private void setUpViewPage(ViewPager viewpager) {
        FragmentPager fragmentPager = new FragmentPager(getChildFragmentManager());
        fragmentPager.addFragment(FragmentTimeline.getInstance(Constants.POSTS, account.id), "POSTS");
        fragmentPager.addFragment(FragmentTimeline.getInstance(Constants.FOLLOWS), "FOLLOWS");
        fragmentPager.addFragment(FragmentTimeline.getInstance(Constants.FOLLOWERS), "FOLLOWERS");

        dataBinding.viewpager.setAdapter(fragmentPager);
    }

    @Override
    public String getCredentials() {

        return getContext()
                .getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
                .getString(getString(R.string.authKey), "");
    }

    public String getUsername() {

        return getContext()
                .getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
                .getString(getString(R.string.CURRENT_USERNAME), "");
    }


}
