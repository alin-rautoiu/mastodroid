package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.Toot;

/**
 * Created by alin on 10.12.2016.
 */

public class ItemFollowerViewModelContract {
    public interface FollowerView {
        Context getContext();

        String getCredentials();
        String getUsername();

        void expandUser(MastodonAccount account);
    }

    public interface ViewModel {
        void destroy();
    }
}
