package eu.theinvaded.mastondroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.ActivityLoginBinding;
import eu.theinvaded.mastondroid.databinding.ActivityReplyBinding;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.viewmodel.ReplyViewModel;
import eu.theinvaded.mastondroid.viewmodel.ReplyViewModelContract;

public class ReplyActivity extends AppCompatActivity implements ReplyViewModelContract.ReplyView {

    public final static String STATUS_ID = "STATUS_ID";
    public final static String IN_REPLY_TO = "IN_REPLY_TO";

    private ActivityReplyBinding binding;
    private Context context;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ReplyActivity.class);
    }

    public static Intent getStartIntent(Context context, Toot statusToreply) {
        Intent intent = new Intent(context, ReplyActivity.class);
        intent.putExtra(STATUS_ID, statusToreply.id);
        intent.putExtra(IN_REPLY_TO, statusToreply.account.username);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_reply);
        binding.setViewModel(new ReplyViewModel(context, this));
        binding.statusEt.addTextChangedListener(binding.getViewModel().charCountWatcher());

        Intent intent = getIntent();
        if (intent != null) {
            long statusId = intent.getLongExtra(STATUS_ID, 0);
            String username = intent.getStringExtra(IN_REPLY_TO);
            binding.getViewModel().setReplyTo(statusId, username);
        }
    }

    @Override
    public String getCredentials() {
        return context
                .getSharedPreferences(getString(R.string.preferences), context.MODE_PRIVATE)
                .getString(getString(R.string.authKey), "");
    }
}
