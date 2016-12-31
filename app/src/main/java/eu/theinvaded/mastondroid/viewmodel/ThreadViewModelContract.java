package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

import eu.theinvaded.mastondroid.model.Toot;

/**
 * Created by alin on 23.12.2016.
 */

public class ThreadViewModelContract {

    public interface ThreadView {
        String getUsername();
        Context getContext();
        String getCredentials();
    }

    public interface ViewModel {
        void destroy();
        void setToot(long tootId);
    }
}
