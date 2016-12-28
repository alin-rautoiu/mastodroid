package eu.theinvaded.mastondroid.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.FragmentTimelineBinding;
import eu.theinvaded.mastondroid.model.Notification;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.ui.activity.ReplyActivity;
import eu.theinvaded.mastondroid.ui.adapter.TimelineAdapter;
import eu.theinvaded.mastondroid.utils.TootComparator;
import eu.theinvaded.mastondroid.viewmodel.TimelineViewModel;
import eu.theinvaded.mastondroid.viewmodel.TimelineViewModelContract;

/**
 * Created by alin on 10.12.2016.
 */
public class FragmentTimeline extends FragmentBase implements TimelineViewModelContract.MainView {

    private static final String TYPE = "TYPE";

    private FragmentTimelineBinding dataBinding;
    private TimelineViewModel timelineViewModel;
    private TimelineViewModelContract.MainView mainView = this;

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    public static FragmentTimeline getInstance(int type) {
        FragmentTimeline fragmentTimeline = new FragmentTimeline();
        Bundle extraArguments = new Bundle();
        extraArguments.putInt(TYPE, type);
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

        timelineViewModel = new TimelineViewModel(mainView, getContext(), null, bundle.getInt(TYPE));
        dataBinding.setMainViewModel(timelineViewModel);
    }

    private void setupRecycler(View rootView) {
        dataBinding.listPeople.setLayoutManager(new LinearLayoutManager(dataBinding.listPeople.getContext()));
        dataBinding.listPeople.setAdapter(new TimelineAdapter(getCredentials()));
        dataBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (dataBinding.listPeople.getAdapter().getItemCount() != 0) {
                    timelineViewModel.refresh(((TimelineAdapter)dataBinding.listPeople.getAdapter()).getLatestId());
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

        Collections.sort(timeline, new TootComparator());

        timelineAdapter.setTimeline(timeline);

        setVisibility();
    }

    private void setVisibility() {
        timelineViewModel.tootProgressIsVisible.set(View.GONE);
        timelineViewModel.tootListIsVisible.set(View.VISIBLE);
    }

    @Override
    public void loadNotifications(List<Notification> notifications) {
        List<Toot> statuses = new ArrayList<>();

        for (Notification notification: notifications) {
            if (notification.status == null) {
                Toot emptyToot = new Toot();
                emptyToot.statusType = StatusType.Follow;
                emptyToot.account = notification.account;
                emptyToot.isNotification = true;
                statuses.add(emptyToot);
            } else {
                notification.status.isNotification = true;
                notification.status.notifiedAccound = notification.account;
                Log.i("Notification type ", notification.type);
                switch (notification.type) {
                    case "follow" :
                        notification.status.statusType = StatusType.Follow;
                        break;
                    case "favourite":
                        notification.status.statusType = StatusType.Favorite;
                        break;
                    case "mention":
                        notification.status.statusType = StatusType.Mention;
                        break;
                    case "reblog":
                        notification.status.reblog = notification.status;
                        notification.status.statusType = StatusType.Boost;
                        break;
                }
                statuses.add(notification.status);
            }
        }

        TimelineAdapter timelineAdapter = (TimelineAdapter) dataBinding.listPeople.getAdapter();
        timelineAdapter.setTimeline(statuses);

        setVisibility();
    }


    @Override
    public String getCredentials() {
        String credentials = getContext()
                .getSharedPreferences(getString(R.string.preferences), getContext().MODE_PRIVATE)
                .getString(getString(R.string.authKey), "");

        return credentials;
    }
}
