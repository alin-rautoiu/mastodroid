package eu.theinvaded.mastondroid.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.Token;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static eu.theinvaded.mastondroid.ui.activity.LoginActivity.CLIENT_ID;
import static eu.theinvaded.mastondroid.ui.activity.LoginActivity.CLIENT_SECRET;
import static eu.theinvaded.mastondroid.ui.activity.LoginActivity.LOGIN_PROCESS;
import static eu.theinvaded.mastondroid.ui.activity.LoginActivity.PASSWORD;
import static eu.theinvaded.mastondroid.ui.activity.LoginActivity.USERNAME;

/**
 * Created by alin on 23.12.2016.
 */

public class LoginViewModel extends BaseObservable implements LoginViewModelContract.ViewModel {

    public ObservableBoolean alreadyHasCredentials;
    public ObservableBoolean isSignIn;
    private Subscription subscription;
    private LoginViewModelContract.LoginView viewModel;


    public LoginViewModel(LoginViewModelContract.LoginView viewModel) {
        isSignIn = new ObservableBoolean(false);
        alreadyHasCredentials = new ObservableBoolean(true);
        this.viewModel = viewModel;
        String credentials = viewModel.getCredentials();
        if (credentials.length() != 0) {
            checkCredentials(credentials);
        } else {
            alreadyHasCredentials.set(false);
        }
    }

    private void checkCredentials(String credentials) {
        MastodroidApplication app = MastodroidApplication.create(viewModel.getContext());
        MastodroidService service = app.getMastodroidService(credentials);
        unsubscribeFromObservable();

        service.verifyCredentials()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<MastodonAccount>() {
                            @Override
                            public void call(MastodonAccount account) {
                                if (account != null) {
                                    viewModel.setUser(account);
                                    viewModel.startMainActivity();
                                }
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                alreadyHasCredentials.set(false);
                                isSignIn.set(false);
                            }
                        }
                );
    }

    @Override
    public void destroy() {
        unsubscribeFromObservable();
        subscription = null;
        viewModel = null;
    }

    public void signIn() {
        isSignIn.set(!isSignIn.get());
        viewModel.clearError();

        MastodroidApplication app = MastodroidApplication.create(viewModel.getContext());
        MastodroidService service = app.getMastodroidLoginService();
        unsubscribeFromObservable();

        if (!isNonBlankInput(CLIENT_SECRET, viewModel.getClientSecret(), LOGIN_PROCESS)) return;
        if (!isNonBlankInput(CLIENT_ID, viewModel.getClientId(), LOGIN_PROCESS)) return;
        if (!isNonBlankInput(USERNAME, viewModel.getUsername())) return;
        if (!isNonBlankInput(PASSWORD, viewModel.getPassword())) return;

        service.SignIn(viewModel.getClientId(),
                viewModel.getClientSecret(),
                "read write follow",
                "password",
                viewModel.getUsername(),
                viewModel.getPassword()
        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<Token>() {
                            @Override
                            public void call(Token token) {
                                viewModel.setAuthKey(token.accessToken);
                                isSignIn.set(!isSignIn.get());
                                checkCredentials(token.accessToken);
                                viewModel.startMainActivity();
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                isSignIn.set(!isSignIn.get());
                                String details = loginExceptionToString(throwable);
                                viewModel.setError(LOGIN_PROCESS, details == null ? R.string.login_failed_message : R.string.login_failed_unknown, details);
                            }
                        }
                );
    }

    private String loginExceptionToString(Throwable e) {
        return e instanceof HttpException ? loginHttpExceptionToString((HttpException) e) : e.toString();
    }

    private String loginHttpExceptionToString(HttpException he) {
        String errorBody;
        try {
            errorBody = he.response().errorBody().string();
        } catch (Throwable convertError) {
            return he.toString() + " and " + convertError.toString();
        }

        // https://github.com/tootsuite/mastodon/pull/550
        if (he.code() == 302 && errorBody.contains("/auth/sign_in"))
            return null;

        String headers = he.response().headers().toString();
        return headers + "\n" + errorBody;
    }

    private Boolean isNonBlankInput(String target, String value) {
        return isNonBlankInput(target, value, null);
    }

    private Boolean isNonBlankInput(String target, String value, String on) {
        on = on == null ? target : on;
        if (value.compareTo("") == 0) {
            viewModel.setError(on, "NO_" + target.toUpperCase() + "_ERROR");
            isSignIn.set(!isSignIn.get());
            return false;
        }

        viewModel.clearError(target);
        return true;
    }

    private void unsubscribeFromObservable() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
