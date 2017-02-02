package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.net.Uri;

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
        void signIn();
        boolean checkAppRegistered();
        void registerApp(String clientId, String clientSecret);
        void authorizeApp(String accessToken);
        Uri.Builder getDomain();
        void domainError();
    }

    public interface ViewModel {
        void destroy();
        void authorizeApp(String clientId, String clientSecret, String authorization_code, String code);
    }
}
