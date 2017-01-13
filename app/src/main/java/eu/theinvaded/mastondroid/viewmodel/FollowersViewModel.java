package eu.theinvaded.mastondroid.viewmodel;

import android.databinding.BaseObservable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.Relationship;
import eu.theinvaded.mastondroid.utils.Constants;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by alin on 09.01.2017.
 */

public class FollowersViewModel extends BaseObservable implements FollowersViewModelContract.ViewModel {

    private static final String TAG = "FollowersViewModel";
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
                .subscribe(getFollowers(), new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: getFollowers", throwable);
                    }
                });
    }

    private void fetchFollowers(long maxId) {

        subscription = service.getFollowersNext(userId, maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getFollowers(), new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: getFollowersNext", throwable);
                    }
                });
    }

    private void fetchFollowing() {

        subscription = service.getFollowing(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getFollowers(), new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: getFollowing", throwable);
                    }
                });
    }

    private void fetchFollowing(long maxId) {

        subscription = service.getFollowingNext(userId, maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getFollowers(), new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: getFollowingNext", throwable);
                    }
                });
    }

    @Override
    public void destroy() {
        userId = 0;
        type = 0;
        app = null;
        service = null;
    }

    @NonNull
    private Action1<List<MastodonAccount>> getFollowers() {
        return new Action1<List<MastodonAccount>>() {
            @Override
            public void call(final List<MastodonAccount> accounts) {
                final ArrayList<Long> userIds = new ArrayList<>();

                rx.Observable
                        .from(accounts)
                        .collect(new Func0<ArrayList<Long>>() {
                            @Override
                            public ArrayList<Long> call() {
                                return null;
                            }
                        }, new Action2<ArrayList<Long>, MastodonAccount>() {

                            @Override
                            public void call(ArrayList<Long> longs, MastodonAccount account) {
                                userIds.add(account.id);
                            }
                        })
                        .subscribe();

                final android.support.v4.util.LongSparseArray<Integer> mappedUsers =
                        new android.support.v4.util.LongSparseArray<>(userIds.size());

                if (userIds.size() > 0) {
                    service.relationships(userIds)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Action1<List<Relationship>>() {
                                @Override
                                public void call(List<Relationship> relationships) {

                                    //TODO Maybe find a more RxJava way to do this?
                                    for (int i = 0; i < userIds.size(); i++) {
                                        mappedUsers.append(accounts.get(i).id, i);
                                    }

                                    for (Relationship relationship: relationships) {
                                        accounts.get(mappedUsers.get(relationship.getId())).relationship = relationship;
                                    }
                                    followersView.loadData(accounts);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e(TAG, "call: relationships", throwable);
                                }
                            });
                } else {
                    followersView.loadData(accounts);
                }
            }


        };
    }
}
