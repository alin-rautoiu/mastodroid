package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;

import eu.theinvaded.mastondroid.ui.activity.ReplyActivity;
import eu.theinvaded.mastondroid.ui.activity.SearchActivity;

/**
 * Created by alin on 26.12.2016.
 */

public class SearchViewModel extends BaseObservable {

    private final Context context;

    public SearchViewModel(Context context) {
        this.context = context;
    }

    public void toot() {
        context.startActivity(ReplyActivity.getStartIntent(context));
    }

    public void search() {
        //context.startActivity(SearchActivity.getStartIntent(context, query));
    }
}
