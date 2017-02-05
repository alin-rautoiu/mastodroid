package eu.theinvaded.mastondroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.ui.fragment.FragmentUser;

public class UserActivity extends AppCompatActivity {

    private static String USER_KEY = "USER_KEY";

    public static Intent getStartIntent(Context context, MastodonAccount user) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(USER_KEY, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        MastodonAccount user = getIntent().getParcelableExtra(USER_KEY);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.user_content, FragmentUser.getInstance(user))
                .commit();
    }

}
