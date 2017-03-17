package eu.theinvaded.mastondroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.ActivitySearchBinding;
import eu.theinvaded.mastondroid.ui.adapter.FragmentPager;
import eu.theinvaded.mastondroid.ui.fragment.FragmentFollowers;
import eu.theinvaded.mastondroid.ui.fragment.FragmentTimeline;
import eu.theinvaded.mastondroid.utils.Constants;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding dataBinding;
    private Context context;

    public static Intent getStartIntent(Context context, String query) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(Constants.QUERY, query);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        setUpViewPage(dataBinding.viewpager);
        context = this;
        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dataBinding.tabs.setupWithViewPager(dataBinding.viewpager);
        dataBinding.searchSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startActivity(SearchActivity.getStartIntent(context, query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setUpViewPage(ViewPager viewpager) {

        String query = "";
        Intent intent = getIntent();
        if (intent != null) {
            query = intent.getExtras().getString(Constants.QUERY);
        }

        FragmentPager fragmentPager = new FragmentPager(getSupportFragmentManager());
        fragmentPager.addFragment(FragmentFollowers.getInstance(Constants.SEARCH, query), "Users");
        fragmentPager.addFragment(FragmentTimeline.getInstance(Constants.HASHTAG, query), "Hashtag");

        viewpager.setAdapter(fragmentPager);
    }
}
