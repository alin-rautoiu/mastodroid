package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;

import eu.theinvaded.mastondroid.ui.activity.ReplyActivity;

/**
 * Created by alin on 26.12.2016.
 */

public class MainViewModel extends BaseObservable {

    private final Context context;

    public MainViewModel(Context context) {
        this.context = context;
    }

    public void toot() {
        context.startActivity(ReplyActivity.getStartIntent(context));
    }
}
