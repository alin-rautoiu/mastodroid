package eu.theinvaded.mastondroid.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.FragmentTimelineBinding;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.ui.adapter.TimelineAdapter;
import eu.theinvaded.mastondroid.utils.Constants;
import eu.theinvaded.mastondroid.utils.PostsRecyclerScrollListener;
import eu.theinvaded.mastondroid.utils.TootComparator;
import eu.theinvaded.mastondroid.viewmodel.TimelineViewModel;
import eu.theinvaded.mastondroid.viewmodel.TimelineViewModelContract;

/**
 * Created by alin on 10.12.2016.
 */
public class FragmentTimeline extends FragmentBase implements TimelineViewModelContract.MainView {

    private static final String TYPE = "TYPE";
    private static final String USER_ID = "USER_ID";

    private FragmentTimelineBinding dataBinding;
    private TimelineViewModel timelineViewModel;
    private TimelineViewModelContract.MainView mainView = this;

    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private long userId;
    private int type;

    public static FragmentTimeline getInstance(int type) {
        FragmentTimeline fragmentTimeline = new FragmentTimeline();
        Bundle extraArguments = new Bundle();
        extraArguments.putInt(TYPE, type);
        fragmentTimeline.setArguments(extraArguments);

        return fragmentTimeline;
    }

    public static FragmentTimeline getInstance(int type, long userId) {
        FragmentTimeline fragmentTimeline = getInstance(type);
        Bundle extraArguments = fragmentTimeline.getArguments();
        extraArguments.putLong(USER_ID, userId);
        fragmentTimeline.setArguments(extraArguments);

        return fragmentTimeline;
    }

    public static FragmentTimeline getInstance(int type, String query) {
        FragmentTimeline fragmentTimeline = new FragmentTimeline();
        Bundle extraArguments = new Bundle();
        extraArguments.putInt(TYPE, type);
        extraArguments.putString(Constants.QUERY, query);
        fragmentTimeline.setArguments(extraArguments);

        return fragmentTimeline;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        View rootView = dataBinding.getRoot();
        setupRecycler();
        initDataBinding();

        return rootView;
    }

    private void initDataBinding() {
        Bundle bundle = this.getArguments();
        type = bundle.getInt(TYPE);
        timelineViewModel = new TimelineViewModel(mainView, getContext(), type);
        userId = getArguments().getLong(USER_ID);
        String query = getArguments().getString(Constants.QUERY);
        timelineViewModel.setHashtag(query);
        dataBinding.setMainViewModel(timelineViewModel);

        if (userId != 0) {
            timelineViewModel.refreshUser(userId);
        } else {
            timelineViewModel.refresh();
        }
    }

    private void setupRecycler() {
        dataBinding.statusesRv
                .setLayoutManager(new LinearLayoutManager(dataBinding.statusesRv.getContext()));
        TimelineAdapter adapter = new TimelineAdapter(getActivity().getSupportFragmentManager());
        adapter.setCredentials(getCredentials());
        adapter.setUsername(getUsername());
        dataBinding.statusesRv.setAdapter(adapter);

        dataBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (dataBinding.statusesRv.getAdapter().getItemCount() == 0) {
                    timelineViewModel.refresh();
                    return;
                }

                timelineViewModel
                        .refresh(((TimelineAdapter) dataBinding.statusesRv
                                .getAdapter())
                                .getLatestId());
            }
        });

        dataBinding.statusesRv
                .addOnScrollListener(
                        new PostsRecyclerScrollListener((LinearLayoutManager) dataBinding.statusesRv.getLayoutManager()) {
                            @Override
                            protected void loadData() {
                                if (userId != 0) {
                                    timelineViewModel
                                            .refreshUser(userId, ((TimelineAdapter) dataBinding.statusesRv
                                                    .getAdapter())
                                                    .getLastId());
                                    return;
                                }

                                timelineViewModel
                                        .bringFromPast(((TimelineAdapter) dataBinding.statusesRv
                                                .getAdapter())
                                                .getLastId());

                            }
                        });
    }

    @Override
    public void loadData(List<Toot> timeline, boolean inFront, boolean isNotifications) {
        TimelineAdapter timelineAdapter = (TimelineAdapter) dataBinding.statusesRv.getAdapter();
        for (Toot toot : timeline) {
            if (toot.reblog != null) {
                toot.statusType = StatusType.Boost;
            }
        }
        if (!isNotifications) {
            Collections.sort(timeline, new TootComparator());
        }
        timelineAdapter.setTimeline(timeline, inFront, isNotifications);
        loading = false;
        setVisibility();
    }

    private void setVisibility() {
        dataBinding.swipeRefreshLayout.setRefreshing(false);
        timelineViewModel.tootProgressIsVisible.set(View.GONE);
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
