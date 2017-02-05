package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

/**
 * Created by alin on 23.12.2016.
 */

public class ReplyViewModelContract {

    public interface ReplyView {
        String getCredentials();

        void goToParent();
        void showError();
    }

    public interface ViewModel {
        void destroy();
    }
}
