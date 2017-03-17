package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.MastodonThread;
import eu.theinvaded.mastondroid.model.Notification;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.ui.activity.ThreadActivity;
import eu.theinvaded.mastondroid.utils.Constants;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by alin on 10.12.2016.
 */

public class TimelineViewModel implements TimelineViewModelContract.ViewModel {

    public ObservableInt tootProgressIsVisible;

    private Subscription subscription;
    private TimelineViewModelContract.MainView mainView;
    private int type;
    private MastodroidApplication app;
    private MastodroidService service;
    private Toot highlightedStatus;
    private String hashtag;

    public TimelineViewModel(@NonNull TimelineViewModelContract.MainView mainView,
                             @NonNull Context context, int type) {

        this.mainView = mainView;
        this.tootProgressIsVisible = new ObservableInt(View.VISIBLE);
        this.type = type;

        app = MastodroidApplication.create(context);
        service = app.getMastodroidService(mainView.getCredentials());
    }

    public TimelineViewModel(ThreadActivity threadActivity,
                             Context context,
                             int thread,
                             Toot highlightedStatus) {

        this(threadActivity, context, thread);
        this.highlightedStatus = highlightedStatus;
    }

    private void fetchPublicTimeline() {

        subscription = service.getPublicTimeLine()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> timeline) {
                                   if (mainView != null) {
                                       mainView.loadData(timeline, false, false);
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

    private void fetchPublicTimelineUpdate(long sinceId) {

        subscription = service.getPublicTimeLineUpdate(sinceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> timeline) {
                                   if (mainView != null) {
                                       mainView.loadData(timeline, true, false);
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

    private void fetchPublicTimelineFromPast(long maxId) {

        subscription = service.getPublicTimeLineFromPast(maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> timeline) {
                                   if (mainView != null) {
                                       mainView.loadData(timeline, false, false);
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

    private void fetchNotifications() {

        subscription = service.getNotifications()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Notification>>() {
                               @Override
                               public void call(List<Notification> notifications) {
                                   if (mainView != null) {

                                       List<Toot> statuses = new ArrayList<>();

                                       for (Notification notification : notifications) {
                                           if (notification.status == null) {
                                               Toot emptyToot = new Toot();
                                               emptyToot.statusType = StatusType.Follow;
                                               emptyToot.account = notification.account;
                                               emptyToot.isNotification = true;
                                               statuses.add(emptyToot);
                                           } else {
                                               notification.status.isNotification = true;
                                               notification.status.notifiedAccound = notification.account;
                                               Log.i("Notification type ", notification.type);
                                               switch (notification.type) {
                                                   case "follow":
                                                       notification.status.statusType = StatusType.Follow;
                                                       break;
                                                   case "favourite":
                                                       notification.status.statusType = StatusType.Favorite;
                                                       break;
                                                   case "mention":
                                                       notification.status.statusType = StatusType.Mention;
                                                       break;
                                                   case "reblog":
                                                       notification.status.reblog = notification.status;
                                                       notification.status.statusType = StatusType.Boost;
                                                       break;
                                               }
                                               statuses.add(notification.status);
                                           }
                                       }

                                       mainView.loadData(statuses, true, true);
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

    private void fetchHome() {

        subscription = service.getHomeTimeLine()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> timeline) {
                                   if (mainView != null) {
                                       mainView.loadData(timeline, false, false);
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

    private void fetchHomeUpdate(long sinceId) {

        subscription = service.getHomeTimeLineUpdate(sinceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> timeline) {
                                   if (mainView != null) {
                                       mainView.loadData(timeline, true, false);
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

    private void fetchHomeFromPast(long maxId) {

        subscription = service.getHomeTimeLineFromPast(maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> timeline) {
                                   if (mainView != null) {
                                       mainView.loadData(timeline, false, false);
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

    private void fetchThread() {
        subscription = service.getThread(highlightedStatus.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<MastodonThread>() {
                               @Override
                               public void call(MastodonThread thread) {
                                   if (mainView != null) {
                                       List<Toot> newList = new ArrayList<Toot>();
                                       newList.addAll(thread.ancestors);
                                       newList.addAll(thread.descendants);
                                       highlightedStatus.isHiglighted = true;
                                       newList.add(highlightedStatus);
                                       mainView.loadData(newList, false, false);
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

    private void fetchUserStatuses(long userId) {
        subscription = service.getStatusesForUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> statuses) {
                                   if (mainView != null) {
                                       mainView.loadData(statuses, false, false);
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

    private void fetchUserStatusesFromPast(long userId, long maxId) {
        subscription = service.getStatusesForUserFromPast(userId, maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> statuses) {
                                   if (mainView != null) {
                                       mainView.loadData(statuses, false, false);
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

    private Toot returnToot(Toot toot) {
        return toot;
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
        mainView = null;
    }

    public void refresh() {
        tootProgressIsVisible.set(View.VISIBLE);
        switch (type) {
            case Constants.HOME:
                fetchHome();
                break;
            case Constants.NOTIFICATIONS:
                fetchNotifications();
                break;
            case Constants.PUBLIC:
                fetchPublicTimeline();
                break;
            case Constants.THREAD:
                fetchThread();
                break;
            case Constants.HASHTAG:
                fetchHashtag();
                break;
        }
    }

    public void refreshUser(long userId) {
        fetchUserStatuses(userId);
    }

    public void refreshUser(long userId, long maxId) {
        fetchUserStatusesFromPast(userId, maxId);
    }

    public void refresh(long sinceId) {
        tootProgressIsVisible.set(View.VISIBLE);
        switch (type) {
            case Constants.HOME:
                fetchHomeUpdate(sinceId);
                break;
            case Constants.NOTIFICATIONS:
                fetchNotifications();
                break;
            case Constants.PUBLIC:
                fetchPublicTimelineUpdate(sinceId);
                break;
            case Constants.HASHTAG:
                fetchHashtag(sinceId);
                break;
        }
    }

    private void fetchHashtag() {
        subscription = service.getHashtagTimeline(hashtag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> statuses) {
                                   if (mainView != null) {
                                       mainView.loadData(statuses, false, false);
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

    private void fetchHashtag(long sinceId) {
        subscription = service.getHashtagTimelineUpdate(hashtag, sinceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> statuses) {
                                   if (mainView != null) {
                                       mainView.loadData(statuses, false, false);
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

    private void fetchHashtagFromPast(long maxId) {
        subscription = service.getHashtagTimelineFromPast(hashtag, maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Toot>>() {
                               @Override
                               public void call(List<Toot> statuses) {
                                   if (mainView != null) {
                                       mainView.loadData(statuses, false, false);
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

    public void bringFromPast(long maxId) {
        switch (type) {
            case Constants.HOME:
                fetchHomeFromPast(maxId);
                break;
            case Constants.PUBLIC:
                fetchPublicTimelineFromPast(maxId);
                break;
            case Constants.HASHTAG:
                fetchHashtagFromPast(maxId);
                break;
        }
    }

    public void setHighlightedStatus(Toot highlightedStatus) {
        this.highlightedStatus = highlightedStatus;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }
}
