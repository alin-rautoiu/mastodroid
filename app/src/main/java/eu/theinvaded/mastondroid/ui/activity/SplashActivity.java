package eu.theinvaded.mastondroid.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eu.theinvaded.mastondroid.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);

        Intent intent = preferences.contains(getString(R.string.authorization_code))
                ? MainActivity.getStartIntent(this)
                : LoginActivity.getStartIntent(this);

        startActivity(intent);
        finish();
    }
}
