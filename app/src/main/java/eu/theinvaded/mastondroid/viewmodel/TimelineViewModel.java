package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.util.List;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.Notification;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.ui.fragment.FragmentMain;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by alin on 10.12.2016.
 */

public class TimelineViewModel implements TimelineViewModelContract.ViewModel {

    public ObservableInt tootListIsVisible;
    public ObservableInt tootProgressIsVisible;

    private Context context;
    private Subscription subscription;
    private TimelineViewModelContract.MainView mainView;
    private int type;
    private MastodroidApplication app;
    private MastodroidService service;

    public TimelineViewModel(@NonNull TimelineViewModelContract.MainView mainView,
                             @NonNull Context context, Subscription subscription, int type) {
        this.context = context;
        this.subscription = subscription;
        this.mainView = mainView;
        this.tootProgressIsVisible = new ObservableInt(View.VISIBLE);
        this.tootListIsVisible = new ObservableInt(View.GONE);
        this.type = type;

        app = MastodroidApplication.create(context);
        service = app.getMastodroidService(mainView.getCredentials());

        refresh();
    }

    public void fetchPublicTimeline() {

        subscription = service.getPublicTimeLine()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> timeline) {
                                   if (mainView != null) {
                                       mainView.loadData(timeline);
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

    public void fetchPublicTimeline(long minId) {

        subscription = service.getPublicTimeLineUpdate(minId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> timeline) {
                                   if (mainView != null) {
                                       mainView.loadData(timeline);
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

    public void fetchNotifications() {

        subscription = service.getNotifications()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Notification>>() {
                               @Override
                               public void call(List<Notification> notifications) {
                                   if (mainView != null) {
                                       mainView.loadNotifications(notifications);
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

    public void fetchHome() {

        subscription = service.getHomeTimeLine()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> timeline) {
                                   if (mainView != null) {
                                       mainView.loadData(timeline);
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

    private void unsubscribeFromObservable() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void destroy() {
        reset();
    }

    private void reset() {
        unsubscribeFromObservable();
        subscription = null;
        context = null;
        mainView = null;
    }

    public void refresh() {
        this.tootProgressIsVisible.set(View.VISIBLE);
        this.tootListIsVisible.set(View.GONE);
        switch (type) {
            case FragmentMain.HOME:
                fetchHome();
                break;
            case FragmentMain.NOTIFICATIONS:
                fetchNotifications();
                break;
            case FragmentMain.PUBLIC:
                fetchPublicTimeline();
                break;
        }
    }

    public void refresh(long minId) {
        this.tootProgressIsVisible.set(View.VISIBLE);
        this.tootListIsVisible.set(View.GONE);
        switch (type) {
            case FragmentMain.HOME:
                fetchHome();
                break;
            case FragmentMain.NOTIFICATIONS:
                fetchNotifications();
                break;
            case FragmentMain.PUBLIC:
                fetchPublicTimeline(minId);
                break;
        }
    }
}
