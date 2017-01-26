package eu.theinvaded.mastondroid.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

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

    public void setError(String target, String error) {
        setError(target, error, null);
    }

    public void setError(String target, String error, String details) {
        // probably better ways to do this, but the android docs are stirred shit
        TextInputLayout target_layout = getLayoutOrFallback(target);
        int errorId = getResId(error, "string");
        String errorStr = errorId == 0 ? getString(R.string.UNKNOWN_ERROR_ID) + error : getString(errorId);
        target_layout.setErrorEnabled(true);
        target_layout.setError(errorStr + (details == null ? "" : ": " + details));
    }

    public void clearError() {
        clearError(null);
    }

    public void clearError(String target) {
        getLayoutOrFallback(target).setErrorEnabled(false);
    }

    private TextInputLayout getLayoutOrFallback(String target) {
        TextInputLayout layout = (TextInputLayout) findViewById(getResId(target + "_layout", "id"));
        return layout == null ? binding.loginProcessLayout : layout;
    }

    private int getResId(String target, String type) {
        return getResources().getIdentifier(target, type, getPackageName());
    }
}
