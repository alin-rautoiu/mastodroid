package eu.theinvaded.mastondroid.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.ActivityLoginBinding;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.viewmodel.LoginViewModel;
import eu.theinvaded.mastondroid.viewmodel.LoginViewModelContract;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity implements LoginViewModelContract.LoginView {

    private ActivityLoginBinding binding;
    private Context context;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        this.context = this;
        preferences =
                context.getSharedPreferences(getString(R.string.preferences), context.MODE_PRIVATE);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        LoginViewModel loginViewModel = new LoginViewModel(this);
        binding.setViewModel(loginViewModel);
    }

    @Override
    public String getUsername() {
        return binding.usernameTv.getText().toString();
    }

    @Override
    public String getPassword() {
        return binding.passwordTv.getText().toString();
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void setAuthKey(String authKey) {
        preferences
                .edit()
                .putString(getString(R.string.authKey), authKey)
                .apply();
    }

    @Override
    public String getCredentials() {
        return preferences.getString(getString(R.string.authKey), "");
    }

    @Override
    public void startMainActivity() {
        startActivity(MainActivity.getStartIntent(context));
        finish();
    }

    @Override
    public String getClientSecret() {
        return getString(R.string.clientSecret);
    }

    @Override
    public String getClientId() {
        return getString(R.string.clientId);
    }

    @Override
    public void setUser(MastodonAccount account) {
        preferences
                .edit()
                .putString(getString(R.string.CURRENT_USERNAME), account.username)
                .apply();
    }


    @Override
    public void showLoginError() {
        Toast.makeText(context, R.string.login_failed_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setNoUsernameError() {
        binding.usernameLayout.setErrorEnabled(true);
        binding.usernameLayout.setError(getString(R.string.NO_USERNAME_ERROR));
    }

    @Override
    public void setNoPasswordError() {
        binding.passwordLayout.setErrorEnabled(true);
        binding.passwordLayout.setError(getString(R.string.NO_PASSWORD_ERROR));
    }

    @Override
    public void clearNoUsernameError() {
        binding.usernameLayout.setErrorEnabled(false);
    }

    @Override
    public void clearNoPasswordError() {
        binding.passwordLayout.setErrorEnabled(false);
    }
}
