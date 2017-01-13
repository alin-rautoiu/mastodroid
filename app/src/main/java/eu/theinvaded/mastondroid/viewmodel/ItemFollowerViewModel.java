package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
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
 * Created by alin on 09.01.2017.
 */

public class ItemFollowerViewModel extends BaseObservable implements ItemFollowerViewModelContract.ViewModel {

    private static final String TAG = "ItemFollowerViewModel";
    public ObservableBoolean isFollowed;

    private MastodonAccount account;
    private ItemFollowerViewModelContract.FollowerView followerView;
    private MastodroidService service;
    private MastodroidApplication app;

    public ItemFollowerViewModel(MastodonAccount account, Context context, ItemFollowerViewModelContract.FollowerView followerView) {
        this.account = account;
        this.followerView = followerView;

        app = MastodroidApplication.create(context);
        service = app.getMastodroidService(followerView.getCredentials());

        isFollowed = new ObservableBoolean(false);
        checkRelationship();
    }

    public String getAvatarUrl() {
        return account.avatar;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_person)
                .into(view);
    }

    public void expandUser() {
        followerView.expandUser(account);
    }

    public void toggleUserRelationship() {
        if (isFollowed.get()) {
            unfollowUser();
        } else {
            followUser();
        }
    }

    public String getUsername() {
        return account.username;
    }

    public String getDisplayName() {
        return account.displayName;
    }

    private void checkRelationship() {
        isFollowed.set(account.relationship.isFollowing());
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
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: followUser",throwable );
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
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: unfollowUser",throwable );
                    }
                });

    }

    @Override
    public void destroy() {
        account = null;
        app = null;
        service = null;
    }
}
