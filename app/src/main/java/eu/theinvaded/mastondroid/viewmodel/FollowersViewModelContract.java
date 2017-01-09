package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

import java.util.List;

import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.Toot;

/**
 * Created by alin on 10.12.2016.
 */

public class FollowersViewModelContract {
    public interface FollowersView {
        Context getContext();

        void loadData(List<MastodonAccount> timeline);
        String getCredentials();
        String getUsername();
    }

    public interface ViewModel {
        void destroy();
    }
}
