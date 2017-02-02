package eu.theinvaded.mastondroid.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.data.RegisterResponse;
import eu.theinvaded.mastondroid.model.Token;
import rx.Observable;
import rx.Subscriber;
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
    private final String appName;
    private final String schema;
    private String host;
    private String scopes;
    private final static String TAG = "LoginViewModel";

    public LoginViewModel(LoginViewModelContract.LoginView viewModel, String appName, String schema, String host, String scopes) {
        this.appName = appName;
        this.schema = schema;
        this.host = host;
        this.scopes = scopes;
        this.viewModel = viewModel;

        isSignIn = new ObservableBoolean(false);
        alreadyHasCredentials = new ObservableBoolean(true);
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
    public void authorizeApp(String clientId, String clientSecret, String authorization_code, String code) {
        service.SignIn(clientId, clientSecret, schema + "://" + host + "/", authorization_code, code)
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

        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(checkConnections());
                subscriber.onCompleted();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean availableConnection) {
                        if (!availableConnection) {
                            viewModel.domainError();
                        } else {
                            if (!viewModel.checkAppRegistered()) {
                                service.registerApp(appName, schema + "://" + host + "/", scopes)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new Action1<RegisterResponse>() {
                                            @Override
                                            public void call(RegisterResponse registerResponse) {
                                                viewModel.registerApp(registerResponse.getClientId(), registerResponse.getClientSecret());
                                                viewModel.signIn();
                                            }
                                        });
                            } else {
                                viewModel.signIn();
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: signIn", throwable);
                    }
                });
    }

    private boolean checkConnections() {
        String domain = viewModel.getDomain().toString();

        try {
            URL url = new URL(domain);

            HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                urlConnection.disconnect();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    private void unsubscribeFromObservable() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
