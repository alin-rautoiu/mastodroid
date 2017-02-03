package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import eu.theinvaded.mastondroid.model.Notification;
import eu.theinvaded.mastondroid.model.Toot;

/**
 * Created by alin on 10.12.2016.
 */

public class TimelineViewModelContract {
    public interface MainView {
        Context getContext();

        void loadData(List<Toot> timeline);
        String getCredentials();
        String getUsername();
    }

    public interface ViewModel {
        void destroy();
    }
}
