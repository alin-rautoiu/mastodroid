package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.parser.BindingExpressionParser;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.Relationship;
import eu.theinvaded.mastondroid.ui.activity.ReplyActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by alin on 07.01.2017.
 */

public class UserViewModel extends BaseObservable implements UserViewModelContract.ViewModel{

    public ObservableBoolean isFollowed;
    UserViewModelContract.UserView userModel;
    MastodonAccount account;

    private MastodroidService service;
    private MastodroidApplication app;

    public String getAvatar () {
        return account.avatar;
    }
    public String getHeader () {
        return account.header;
    }

    public String getDisplayName () {
        return account.displayName;
    }

    public String getUsername () {
        return account.username;
    }

    public Spanned getNote() {
        if (account.note == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(account.note, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(account.note);
        }
    }

    public UserViewModel(UserViewModelContract.UserView userModel, MastodonAccount account) {
        this.userModel = userModel;
        this.account = account;

        app = MastodroidApplication.create(userModel.getContext());
        service = app.getMastodroidService(userModel.getCredentials());

        isFollowed = new ObservableBoolean(false);
        checkRelationship();
    }

    public void toggleUserRelationship() {
        if (isFollowed.get()) {
            unfollowUser();
        } else {
            followUser();
        }
    }

    private void checkRelationship() {
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(account.id);
        service.relationships(ids)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Relationship>>() {
                    @Override
                    public void call(List<Relationship> relationships) {
                        isFollowed.set(relationships.get(0).isFollowing());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("checkrelationships", "call: ", throwable);
                    }
                });
    }


    private void followUser() {
        service.followUser(account.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Relationship>() {
                    @Override
                    public void call(Relationship relationship) {
                        isFollowed.set(true);
                    }
                });
    }

    private void unfollowUser() {
        service.unfollowUser(account.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Relationship>() {
                    @Override
                    public void call(Relationship relationship) {
                        isFollowed.set(false);
                    }
                });

    }

    public void toot() {
        Context context = userModel.getContext();
        context.startActivity(ReplyActivity.getStartIntent(context));
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_person)
                .into(view);
    }

    @Override
    public void destroy() {

    }
}
