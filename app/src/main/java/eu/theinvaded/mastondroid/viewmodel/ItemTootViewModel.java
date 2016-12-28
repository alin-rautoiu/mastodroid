package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.database.Observable;
import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by alin on 10.12.2016.
 */

public class ItemTootViewModel extends BaseObservable implements TootViewModelContract.ViewModel {

    private Toot toot;
    private Context context;
    private Subscription subscription;
    public ObservableBoolean isFavorited;
    public ObservableBoolean reblogged;

    public ObservableInt statusTypeVisible;
    public ObservableInt statusTypeBoost;
    public ObservableInt statusTypeFavorite;
    public ObservableInt statusTypeFollow;

    public String statusTypeMessage;

    TootViewModelContract.TootView tootView;
    MastodroidService service;
    MastodroidApplication app;

    public ItemTootViewModel(Toot toot, Context context, TootViewModelContract.TootView tootView) {
        this.toot = toot;
        this.context = context;
        this.tootView = tootView;

        isFavorited = new ObservableBoolean(false);
        reblogged = new ObservableBoolean(this.toot.reblogged);
        statusTypeMessage = "";
        statusTypeVisible = new ObservableInt(View.GONE);
        statusTypeFavorite = new ObservableInt(View.GONE);
        statusTypeBoost = new ObservableInt(View.GONE);
        statusTypeFollow = new ObservableInt(View.GONE);

        app = MastodroidApplication.create(context);
        service = app.getMastodroidService(tootView.getCredentials());
    }

    public String getUsername() {
        if (toot.reblog == null) {
            return toot.account.username;
        } else {
            return toot.reblog.account.username;
        }
    }

    public String getDisplayName() {
        if (toot.reblog == null) {
            return toot.account.displayName;
        } else {
            return toot.reblog.account.displayName;
        }
    }

    public String getStatusTypeMessage() {
        String status = "";
        statusTypeFavorite.set(View.GONE);
        statusTypeBoost.set(View.GONE);

        if (toot.statusType == null)
            return status;

        statusTypeVisible.set(View.VISIBLE);
        switch (toot.statusType) {
            case Boost:
                statusTypeBoost.set(View.VISIBLE);
                status = getStatusName() + " boosted";
                break;
            case Favorite:
                statusTypeFavorite.set(View.VISIBLE);
                status = getStatusName() + " favourited your status";
                break;
            case Follow:
                statusTypeFollow.set(View.VISIBLE);
                status = getStatusName() + " followed you";
                break;
            case Mention:
                status = "Mentioned";
                break;
        }
        return status;
    }

    private String getStatusName() {
        MastodonAccount account = toot.notifiedAccound != null ? toot.notifiedAccound : toot.account;
        return account.displayName != null && account.displayName.length() != 0
                ? account.displayName
                : account.username;
    }

    public int getStatusVisibility() {
        return toot.statusType == null || toot.statusType != StatusType.Follow
                ? View.VISIBLE
                : View.GONE;
    }

    public Spanned getContent() {
        String content = "";
        if (toot.statusType == StatusType.Boost) {
            content = toot.reblog.content;
            statusTypeVisible.set(View.VISIBLE);
        } else if (toot.statusType != StatusType.Follow) {
            content = toot.content;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(content);
        }
    }

    public int getButtonBarVisibility() {
        if (toot.isNotification) {
            return View.GONE;
        } else {
            return View.VISIBLE;
        }
    }

    public void favoriteStatus() {

        if (!toot.favorited) {
            subscription = service.favoriteStatus(toot.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Toot>() {
                                   @Override
                                   public void call(Toot favoritedStatus) {
                                       tootView.setStatusFavorite(favoritedStatus);
                                       toot.favorited = true;
                                   }
                               }
                    );
        } else {
            subscription = service.unfavoriteStatus(toot.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Toot>() {
                                   @Override
                                   public void call(Toot favoritedStatus) {
                                       tootView.setStatusUnfavorite(favoritedStatus);
                                       toot.favorited = false;
                                   }
                               }
                    );
        }
    }

    public void reblogToot() {

        if (!reblogged.get()) {
            subscription = service.reblogStatus(toot.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Toot>() {
                                   @Override
                                   public void call(Toot boostedStatus) {
                                       reblogged.set(true);
                                   }
                               },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e("Relbog: ", throwable.getMessage());
                                }
                            }
                    );
        } else {
            subscription = service.unreblogStatus(toot.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Toot>() {
                                   @Override
                                   public void call(Toot unboostedStatus) {
                                       reblogged.set(false);
                                   }
                               },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e("Unreblog: ", throwable.getMessage());
                                }
                            }
                    );
        }
    }

    public void reply() {
        if (toot.statusType == StatusType.Boost) {
            tootView.reply(toot.reblog);
        } else {
            tootView.reply(toot);
        }
    }

    public Toot getToot() {
        return toot;
    }

    public void setToot(Toot toot) {
        this.toot = toot;
        notifyChange();
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
        tootView = null;
    }
}
