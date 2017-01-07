package eu.theinvaded.mastondroid.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItemIndex, visibleItemCount, totalItemCount;
    private long userId;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        View rootView = dataBinding.getRoot();
        setupRecycler(rootView);
        initDataBinding();

        return rootView;
    }

    private void initDataBinding() {
        Bundle bundle = this.getArguments();

        timelineViewModel = new TimelineViewModel(mainView, getContext(), bundle.getInt(TYPE));
        userId = getArguments().getLong(USER_ID);
        if (userId != 0) {
            timelineViewModel.refreshUser(userId);
        } else {
            timelineViewModel.refresh();
        }
        dataBinding.setMainViewModel(timelineViewModel);
    }

    private void setupRecycler(View rootView) {
        dataBinding.listPeople
                .setLayoutManager(new LinearLayoutManager(dataBinding.listPeople.getContext()));
        TimelineAdapter adapter = new TimelineAdapter();
        adapter.setCredentials(getCredentials());
        adapter.setUsername(getUsername());
        dataBinding.listPeople.setAdapter(adapter);

        dataBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (dataBinding.listPeople.getAdapter().getItemCount() != 0) {
                    timelineViewModel
                            .refresh(((TimelineAdapter)dataBinding.listPeople
                                    .getAdapter())
                                    .getLatestId());
                } else {
                    timelineViewModel.refresh();
                }
                dataBinding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        dataBinding.listPeople.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager =
                        (LinearLayoutManager) dataBinding.listPeople.getLayoutManager();

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItemIndex = linearLayoutManager.findFirstVisibleItemPosition();

                //synchronize loading state when item count changes
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= firstVisibleItemIndex) {
                    // Loading NOT in progress and end of list has been reached
                    // also triggered if not enough items to fill the screen
                    // if you start loading
                    loading = true;
                    timelineViewModel
                            .bringFromPast(((TimelineAdapter)dataBinding.listPeople
                                    .getAdapter())
                                    .getLastId());
                }
            }
        });
    }

    @Override
    public void loadData(List<Toot> timeline) {
        TimelineAdapter timelineAdapter = (TimelineAdapter) dataBinding.listPeople.getAdapter();
        for (Toot toot: timeline) {
            if (toot.reblog != null) {
                toot.statusType = StatusType.Boost;
            }
        }

        timelineAdapter.setTimeline(timeline);
        Collections.sort(timeline, new TootComparator());
        loading = false;
        setVisibility();
    }

    private void setVisibility() {
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
