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
        void showLoginError();
        void setNoUsernameError();
        void setNoPasswordError();
        void clearNoUsernameError();
        void clearNoPasswordError();
    }

    public interface ViewModel {
        void destroy();
    }
}
