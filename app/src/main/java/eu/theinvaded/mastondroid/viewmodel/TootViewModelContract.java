package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

import java.util.List;

import eu.theinvaded.mastondroid.model.Toot;

/**
 * Created by alin on 10.12.2016.
 */

public class TootViewModelContract {
    public interface TootView {
        Context getContext();

        void setStatusFavorite(Toot id);
        void setStatusUnfavorite(Toot favoritedStatus);
        void reply(Toot toot);
        String getCredentials();
    }

    public interface ViewModel {
        void destroy();
    }
}
