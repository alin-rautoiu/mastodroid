package eu.theinvaded.mastondroid.viewmodel;

import android.accounts.Account;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import eu.theinvaded.mastondroid.BR;
import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.data.RegisterResponse;
import eu.theinvaded.mastondroid.databinding.ActivityLoginBinding;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.Token;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by alin on 23.12.2016.
 */

public class LoginViewModel extends BaseObservable implements LoginViewModelContract.ViewModel {

    public ObservableBoolean alreadyHasCredentials;
    public ObservableBoolean isSignIn;
    private Subscription subscription;
    private LoginViewModelContract.LoginView viewModel;
    private final MastodroidApplication app;
    private final MastodroidService service;

    public LoginViewModel(LoginViewModelContract.LoginView viewModel) {
        isSignIn = new ObservableBoolean(false);
        alreadyHasCredentials = new ObservableBoolean(true);
        this.viewModel = viewModel;

        app = MastodroidApplication.create(viewModel.getContext());
        service = app.getMastodroidLoginService();
    }

    @Override
    public void destroy() {
        unsubscribeFromObservable();
        subscription = null;
        viewModel = null;
    }

    @Override
    public void signIn(String clientId, String clientSecret, String authorization_code, String code) {
        service.SignIn(clientId, clientSecret, "eu.theinvaded.mastondroid://oauth2redirect/", authorization_code, code)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Token>() {
                    @Override
                    public void call(Token token) {
                        viewModel.authorizeApp(token.accessToken);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("LoginViewModel: signIn", throwable.getMessage(), throwable);
                    }
                });
    }

    public void signIn() {
        //isSignIn.set(true);
        if (!viewModel.checkAppRegistered()) {
            service.registerApp("Mastodroid", "eu.theinvaded.mastondroid://oauth2redirect/", "read write follow")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<RegisterResponse>() {
                        @Override
                        public void call(RegisterResponse registerResponse) {
                            viewModel.registerApp(registerResponse.getClientId(), registerResponse.getClientSecret());
                            viewModel.signIn("");
                        }
                    });
        } else {
            viewModel.signIn("");
        }
    }

    private void unsubscribeFromObservable() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
