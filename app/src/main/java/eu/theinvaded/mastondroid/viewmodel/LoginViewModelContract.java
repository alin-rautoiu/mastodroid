package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

import eu.theinvaded.mastondroid.model.MastodonAccount;

/**
 * Created by alin on 23.12.2016.
 */

public class LoginViewModelContract {

    public interface LoginView {
        String getUsername();
        String getPassword();
        Context getContext();
        void setAuthKey(String authKey);
        String getCredentials();
        void startMainActivity();
        String getClientSecret();
        String getClientId();
        void setUser(MastodonAccount account);
        void setError(String type, String error);
        void setError(String type, String error, String details);
        void clearError();
        void clearError(String target);
    }

    public interface ViewModel {
        void destroy();
    }
}
