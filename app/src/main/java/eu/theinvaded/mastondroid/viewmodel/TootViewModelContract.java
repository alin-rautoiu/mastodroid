package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.widget.TextView;

import java.util.List;

import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.Toot;

/**
 * Created by alin on 10.12.2016.
 */

public class TootViewModelContract {
    public interface TootView {
        Context getContext();

        void reply(Toot toot);
        void startThread(Toot toot);

        String getCredentials();
        String getUsername();

        void expandUser(MastodonAccount account);
        void startSearchActivity(String substring);
    }

    public interface ViewModel {
        void destroy();
    }
}
