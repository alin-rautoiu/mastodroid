package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

import eu.theinvaded.mastondroid.model.MastodonAccount;

/**
 * Created by alin on 23.12.2016.
 */

public class LoginViewModelContract {

    static final String LOGIN_PROCESS = "login_process";
    static final String CLIENT_SECRET = "client_secret";
    static final String CLIENT_ID = "client_id";
    static final String USERNAME = "username";
    static final String PASSWORD = "password";

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

        void setError(String type, int error, String details);

        void setError(String type, String error, String details);

        void clearError();

        void clearError(String target);
    }

    public interface ViewModel {
        void destroy();
    }
}
