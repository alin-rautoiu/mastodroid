package eu.theinvaded.mastondroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.ActivityLoginBinding;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.utils.StringUtils;
import eu.theinvaded.mastondroid.viewmodel.LoginViewModel;
import eu.theinvaded.mastondroid.viewmodel.LoginViewModelContract;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity implements LoginViewModelContract.LoginView {

    private ActivityLoginBinding binding;
    private Context context;
    private SharedPreferences preferences;
    private String customSchema;
    private String host;
    private String appName;
    private String scopes;
    private String domain;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        this.context = this;
        preferences =
                context.getSharedPreferences(getString(R.string.preferences), context.MODE_PRIVATE);

        customSchema = getString(R.string.custom_schema);
        host = getString(R.string.host);
        appName = getString(R.string.app_name);
        scopes = getString(R.string.scopes);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = new LoginViewModel(this, appName, customSchema, host, scopes);
        binding.setViewModel(loginViewModel);

        Uri uri = getIntent().getData();
        treatAuthorization(uri);

        if (preferences.contains(getString(R.string.authKey))) {
            startMainActivity();
        }
    }

    private void treatAuthorization(Uri uri) {
        if (uri != null) {
            String clientId = preferences.getString(getString(R.string.clientId), "");
            String clientSecret = preferences.getString(getString(R.string.clientSecret), "");
            String code = uri.getQueryParameter(getString(R.string.code));

            loginViewModel.authorizeApp(clientId, clientSecret, getString(R.string.authorization_code), code);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if (preferences == null) {
            preferences =
                    context.getSharedPreferences(getString(R.string.preferences), context.MODE_PRIVATE);
        }

    }

    @Override
    public Context getContext() {
        return context;
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
    public void setUser(MastodonAccount account) {
        preferences
                .edit()
                .putString(getString(R.string.CURRENT_USERNAME), account.username)
                .apply();
    }

    @Override
    public void signIn() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", preferences.getString(getString(R.string.clientId), ""));
        parameters.put("redirect_uri", customSchema + "://" + host + "/");
        parameters.put("response_type", "code");
        parameters.put("scope", "read write follow");
        String queryParameters;
        try {
            queryParameters = toQueryString(parameters);
        } catch (UnsupportedEncodingException e) {
            //TODO: No clue how to handle this error case??
            assert (false);
            return;
        }

        String domain = getDomain();

        String url = domain
                + getString(R.string.api_authorize) + "?"
                + queryParameters;
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        startActivity(viewIntent);
    }

    @Override
    public String getDomain() {
        String domain = binding.instanceEt.getText().toString();
        if (StringUtils.isNullOrEmpty(domain)) {
            domain = "https://mastodon.social/";
        } else {
            domain = "https://" + cleanDomain(domain) + "/";
        }
        return domain;
    }

    @Override
    public void domainError() {
        binding.instanceTil.setErrorEnabled(true);
        binding.instanceTil.setError(getString(R.string.server_connection_error));
    }

    private String cleanDomain(String domain) {
        domain = domain.replace("https://", "");
        domain = domain.replace("http://", "");
        domain = domain.replaceAll(" ", "");

        return domain;
    }

    @Override
    public boolean checkAppRegistered() {
        return preferences.contains(getString(R.string.clientId))
                && preferences.contains(getString(R.string.clientSecret));
    }

    @Override
    public void registerApp(String clientId, String clientSecret) {
        preferences.edit()
                .putString(getString(R.string.clientId), clientId)
                .putString(getString(R.string.clientSecret), clientSecret)
                .apply();
    }

    @Override
    public void authorizeApp(String accessToken) {
        preferences.edit()
                .putString(getString(R.string.authKey), accessToken)
                .apply();

        startMainActivity();
    }

    private String toQueryString(Map<String, String> parameters)
            throws UnsupportedEncodingException {
        StringBuilder s = new StringBuilder();
        String between = "";
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            s.append(between);
            s.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            s.append("=");
            s.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            between = "&";
        }
        return s.toString();
    }
}
