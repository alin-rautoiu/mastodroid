package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;

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
    }

    public interface ViewModel {
        void destroy();
    }
}
