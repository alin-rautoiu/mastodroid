package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.Toot;

/**
 * Created by alin on 10.12.2016.
 */

public class UserViewModelContract {
    public interface UserView {
        Context getContext();

        String getCredentials();
        String getUsername();
    }

    public interface ViewModel {
        void destroy();
    }
}
