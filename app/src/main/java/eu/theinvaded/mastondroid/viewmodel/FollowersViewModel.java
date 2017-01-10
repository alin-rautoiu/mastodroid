package eu.theinvaded.mastondroid.viewmodel;

import android.databinding.BaseObservable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.utils.Constants;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by alin on 09.01.2017.
 */

public class FollowersViewModel extends BaseObservable implements FollowersViewModelContract.ViewModel {

    private int type;
    private long userId;
    private MastodroidApplication app;
    private MastodroidService service;
    private Subscription subscription;
    private FollowersViewModelContract.FollowersView followersView;
    private String nextFollowers;
    private String nextFollowing;

    public FollowersViewModel(@NonNull FollowersViewModelContract.FollowersView followersView,
                              long userId,
                              int type) {
        app = MastodroidApplication.create(followersView.getContext());
        service = app.getMastodroidService(followersView.getCredentials());
        this.type = type;
        this.userId = userId;
        this.followersView = followersView;
    }

    public void populateList() {
        switch (type) {
            case Constants.FOLLOWERS:
                fetchFollowers();
                break;
            case Constants.FOLLOWS:
                fetchFollowing();
                break;
        }
    }

    public void populateList(long maxId) {
        switch (type) {
            case Constants.FOLLOWERS:
                fetchFollowers(maxId);
                break;
            case Constants.FOLLOWS:
                fetchFollowing(maxId);
                break;
        }
    }

    private void fetchFollowers() {

        subscription = service.getFollowers(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<MastodonAccount>>() {
                               @Override
                               public void call(List<MastodonAccount> followers) {
                                   if (followersView != null) {
                                       followersView.loadData(followers);
                                   }
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e("Error", throwable.getMessage());
                            }
                        }
                );
    }

    private void fetchFollowers(long maxId) {

        subscription = service.getFollowersNext(userId, maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<MastodonAccount>>() {
                               @Override
                               public void call(List<MastodonAccount> followers) {
                                   if (followersView != null) {
                                       followersView.loadData(followers);
                                   }
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e("Error", throwable.getMessage());
                            }
                        }
                );
    }

    private void fetchFollowing() {

        subscription = service.getFollowing(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<MastodonAccount>>() {
                               @Override
                               public void call(List<MastodonAccount> following) {
                                   if (followersView != null) {
                                       followersView.loadData(following);
                                   }
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e("Error", throwable.getMessage());
                            }
                        }
                );
    }

    private void fetchFollowing(long maxId) {

        subscription = service.getFollowingNext(userId, maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<MastodonAccount>>() {
                               @Override
                               public void call(List<MastodonAccount> following) {
                                   if (followersView != null) {
                                       followersView.loadData(following);
                                   }
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e("Error", throwable.getMessage());
                            }
                        }
                );
    }

    @Override
    public void destroy() {
        userId = 0;
        type = 0;
        app = null;
        service = null;
    }
}
