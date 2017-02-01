package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

import eu.theinvaded.mastondroid.model.MastodonAccount;

/**
 * Created by alin on 23.12.2016.
 */

public class LoginViewModelContract {

    public interface LoginView {
        Context getContext();
        String getCredentials();
        void startMainActivity();
        void setUser(MastodonAccount account);
        void signIn(String node);
        boolean checkAppRegistered();
        void registerApp(String clientId, String clientSecret);
        void authorizeApp(String accessToken);
    }

    public interface ViewModel {
        void destroy();
        void signIn(String clientId, String clientSecret, String authorization_code, String code);
    }
}
