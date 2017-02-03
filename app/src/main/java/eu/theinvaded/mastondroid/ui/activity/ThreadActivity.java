package eu.theinvaded.mastondroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.Collections;
import java.util.List;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.FragmentTimelineBinding;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.ui.adapter.TimelineAdapter;
import eu.theinvaded.mastondroid.ui.fragment.FragmentMain;
import eu.theinvaded.mastondroid.utils.Constants;
import eu.theinvaded.mastondroid.utils.TootComparator;
import eu.theinvaded.mastondroid.viewmodel.TimelineViewModel;
import eu.theinvaded.mastondroid.viewmodel.TimelineViewModelContract;

public class ThreadActivity extends AppCompatActivity implements TimelineViewModelContract.MainView {

    private Context context;
    private TimelineViewModel timelineViewModel;
    private boolean loading;
    private Toot highlightedStatus;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ThreadActivity.class);
    }

    public static Intent getStartIntent(Context context, Toot statusToreply) {
        Intent intent = new Intent(context, ThreadActivity.class);
        intent.putExtra("STATUS_ID", statusToreply);

        return intent;
    }

    private FragmentTimelineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        Intent intent = getIntent();
        if (intent != null) {
            highlightedStatus = intent.getParcelableExtra("STATUS_ID");
        }

        setBinding();
    }

    private void setBinding() {
        timelineViewModel = new TimelineViewModel(this, context,
                Constants.THREAD,
                highlightedStatus);
        timelineViewModel.refresh();

        TimelineAdapter adapter = new TimelineAdapter(getSupportFragmentManager());
        adapter.setCredentials(getCredentials());
        adapter.setUsername(getUsername());

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_timeline);
        binding.setMainViewModel(timelineViewModel);
        binding.statusesRv.setAdapter(adapter);
        binding.statusesRv.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL,
                false));
        binding.swipeRefreshLayout.setEnabled(false);
    }

    public String getUsername() {

        return getContext()
                .getSharedPreferences(getString(R.string.preferences), getContext().MODE_PRIVATE)
                .getString(getString(R.string.CURRENT_USERNAME), "");
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void loadData(List<Toot> timeline, boolean inFront, boolean isNotifications) {
        TimelineAdapter timelineAdapter = (TimelineAdapter) binding.statusesRv.getAdapter();
        for (Toot toot: timeline) {
            if (toot.reblog != null) {
                toot.statusType = StatusType.Boost;
            }
        }

        timelineAdapter.setTimeline(timeline, inFront, isNotifications);
        if(!isNotifications) {
            Collections.sort(timeline, new TootComparator(true));
        }
        this.loading = false;
        setVisibility();
    }

    private void setVisibility() {
        timelineViewModel.tootProgressIsVisible.set(View.GONE);
    }

    @Override
    public String getCredentials() {
        return context
                .getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE)
                .getString(getString(R.string.authKey), "");
    }
}
