package eu.theinvaded.mastondroid.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
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

        MastodroidApplication app = MastodroidApplication.create(viewModel.getContext());
        MastodroidService service = app.getMastodroidLoginService();
        unsubscribeFromObservable();

        if (!isNonBlankInput("client_secret", viewModel.getClientSecret(), "login_process")) return;
        if (!isNonBlankInput("client_id", viewModel.getClientId(), "login_process")) return;
        if (!isNonBlankInput("username", viewModel.getUsername())) return;
        if (!isNonBlankInput("password", viewModel.getPassword())) return;

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

                                viewModel.showLoginError();
                            }
                        }
                );
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
