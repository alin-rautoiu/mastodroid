package eu.theinvaded.mastondroid.ui.fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.FragmentFollowersBinding;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.ui.adapter.FollowersAdapter;
import eu.theinvaded.mastondroid.ui.adapter.TimelineAdapter;
import eu.theinvaded.mastondroid.utils.Constants;
import eu.theinvaded.mastondroid.utils.PostsRecyclerScrollListener;
import eu.theinvaded.mastondroid.viewmodel.FollowersViewModel;
import eu.theinvaded.mastondroid.viewmodel.FollowersViewModelContract;
import eu.theinvaded.mastondroid.viewmodel.TimelineViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFollowers extends Fragment implements FollowersViewModelContract.FollowersView{

    private static final String TYPE = "TYPE";
    private static final String USER_ID = "USER_ID";

    private FragmentFollowersBinding dataBinding;
    private FollowersViewModel followersViewModel;
    private FollowersViewModelContract.FollowersView followersView = this;

    private long userId;
    private int type;
    private int visibleItemCount;
    private int totalItemCount;
    private int firstVisibleItemIndex;
    private boolean loading;
    private int previousTotal;

    public static FragmentFollowers getInstance(int type) {
        FragmentFollowers followersFragment = new FragmentFollowers();
        Bundle extraArguments = new Bundle();
        extraArguments.putInt(TYPE, type);
        followersFragment.setArguments(extraArguments);

        return followersFragment;
    }

    public static FragmentFollowers getInstance(int type, long userId) {
        FragmentFollowers followersFragment = new FragmentFollowers();
        Bundle extraArguments = new Bundle();
        extraArguments.putInt(TYPE, type);
        extraArguments.putLong(USER_ID, userId);
        followersFragment.setArguments(extraArguments);

        return followersFragment;
    }

    public static FragmentFollowers getInstance(int type, String query) {
        FragmentFollowers followersFragment = new FragmentFollowers();
        Bundle extraArguments = new Bundle();
        extraArguments.putInt(TYPE, type);
        extraArguments.putString(Constants.QUERY, query);
        followersFragment.setArguments(extraArguments);

        return followersFragment;
    }

    public FragmentFollowers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_followers, container, false);
        View rootView = dataBinding.getRoot();
        setupRecycler(rootView);
        initDataBinding();

        return rootView;
    }

    private void initDataBinding() {
        Bundle bundle = this.getArguments();

        followersViewModel = new FollowersViewModel(this, bundle.getLong(USER_ID), bundle.getInt(TYPE));
        followersViewModel.setQuery(bundle.getString(Constants.QUERY));
        followersViewModel.populateList();
    }

    private void setupRecycler(View rootView) {
        dataBinding.followRv
                .setLayoutManager(new LinearLayoutManager(dataBinding.followRv.getContext()));
        FollowersAdapter adapter = new FollowersAdapter();
        dataBinding.followRv.setAdapter(adapter);

        dataBinding
                .followRv
                .addOnScrollListener(
                        new PostsRecyclerScrollListener((LinearLayoutManager)dataBinding.followRv.getLayoutManager()) {
            @Override
            protected void loadData() {
                followersViewModel
                        .populateList(((FollowersAdapter)dataBinding.followRv
                                .getAdapter())
                                .getLastId());
            }
        });
    }

    @Override
    public void loadData(List<MastodonAccount> accounts) {
        ((FollowersAdapter)dataBinding.followRv.getAdapter()).setAccountList(accounts);
    }

    @Override
    public String getCredentials() {

        return getContext()
                .getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
                .getString(getString(R.string.authKey), "");
    }

    @Override
    public String getUsername() {

        return getContext()
                .getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
                .getString(getString(R.string.CURRENT_USERNAME), "");
    }
}
