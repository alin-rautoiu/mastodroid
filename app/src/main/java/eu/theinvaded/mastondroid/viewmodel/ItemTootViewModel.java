package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.utils.StringUtils;
import rx.Observer;
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
    public ObservableBoolean isHighlighted;

    public ObservableInt statusTypeVisible;
    public ObservableInt statusTypeBoost;
    public ObservableInt statusTypeFavorite;
    public ObservableInt statusTypeFollow;


    private TootViewModelContract.TootView tootView;
    private MastodroidService service;
    private MastodroidApplication app;

    public ItemTootViewModel(Toot toot, Context context, TootViewModelContract.TootView tootView) {
        this.toot = toot;
        this.context = context;
        this.tootView = tootView;

        reblogged = new ObservableBoolean(this.toot.statusType == StatusType.Boost
                ? toot.reblog.reblogged
                : toot.reblogged);
        isFavorited = new ObservableBoolean(this.toot.statusType == StatusType.Boost
                ? toot.reblog.favorited
                : toot.favorited);

        isHighlighted = new ObservableBoolean(this.toot.isHiglighted);

        statusTypeVisible = new ObservableInt(View.GONE);
        statusTypeFavorite = new ObservableInt(View.GONE);
        statusTypeBoost = new ObservableInt(View.GONE);
        statusTypeFollow = new ObservableInt(View.GONE);

        app = MastodroidApplication.create(context);
        service = app.getMastodroidService(tootView.getCredentials());
        this.context = context;
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
        statusTypeFollow.set(View.GONE);

        if (toot.statusType == null)
            return status;

        //statusTypeVisible.set(View.VISIBLE);
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
        final String content;
        String contentTemp;
        if (toot.statusType == StatusType.Boost) {
            content = toot.reblog.content;
            statusTypeVisible.set(View.VISIBLE);
        } else if (toot.statusType != StatusType.Follow) {
            content = toot.content;
        } else {
            content = "";
        }

        final Spannable spanned;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spanned = (Spannable) Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = (Spannable) Html.fromHtml(content);
        }
        // Restyle link: remove underline and change color
        // Also handle clicks
        for (final URLSpan span : spanned.getSpans(0, spanned.length(), URLSpan.class)) {
            spanned.setSpan(new URLSpan(span.getURL()) {
                @Override
                public void onClick(View widget) {
                    int lowerBound = spanned.getSpanStart(this) - 1;
                    String spanString = spanned.toString().substring(lowerBound < 0 ? 0 : lowerBound,
                            spanned.getSpanEnd(this));
                    if (isProfileLInk(spanString, span.getURL())) {
                        handleUsernameLink(spanString.substring(1));
                    } else {
                        super.onClick(widget);
                    }
                }

                @Override
                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                    tp.setColor(ContextCompat.getColor(context, R.color.colorLink));
                }


                boolean isProfileLInk(String linkText, String url) {
                    return linkText.charAt(0) == '@';
                }
            }, spanned.getSpanStart(span), spanned.getSpanEnd(span), 0);
            spanned.removeSpan(span);
        }
        final Spannable spannedTrimmed;
        spannedTrimmed = StringUtils.trimSpannable((SpannableStringBuilder) spanned);
        return spannedTrimmed;
    }


    public int getButtonBarVisibility() {
        if (toot.statusType == StatusType.Follow) {
            return View.GONE;
        } else {
            return View.VISIBLE;
        }
    }

    public String getAvatarUrl() {
        return toot.reblog == null
                ? toot.account.avatar
                : toot.reblog.account.avatar;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_person)
                .into(view);
    }

    public void favoriteStatus() {

        if (!toot.favorited) {
            subscription = service.favoriteStatus(toot.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Toot>() {
                                   @Override
                                   public void call(Toot favoritedStatus) {
                                       toot = favoritedStatus;
                                       isFavorited.set(favoritedStatus.favorited);
                                   }
                               },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e("Favorite: ", throwable.getMessage());
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
                                       toot = favoritedStatus;
                                       isFavorited.set(favoritedStatus.favorited);
                                   }
                               },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e("Unfavorite: ", throwable.getMessage());
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
                                       toot = boostedStatus;
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
                                       toot = unboostedStatus;
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

    public void startThread() {
        tootView.startThread(toot);
    }

    private void unsubscribeFromObservable() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void expandUser() {
        if (toot.statusType == StatusType.Boost) {
            tootView.expandUser(toot.reblog.account);
        } else {
            tootView.expandUser(toot.account);
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

    void handleUsernameLink(String username) {
        MastodonAccount mentionedUser = null;
        List<MastodonAccount> mentions;
        if (toot.reblog != null) {
            mentions = toot.reblog.mentions;
        } else {
            mentions = toot.mentions;
        }

        // First check if there's a mention from our domain
        for (MastodonAccount mention : mentions) {
            if (mention.acct.equals(username)) {
                mentionedUser = mention;
                break;
            }
        }

        // now check mentions from other domains
        // not perfect, we should probably check the link
        if (mentionedUser == null) {
            for (MastodonAccount mention : mentions) {
                String shortUsername = mention.acct.split("@")[0];
                if (shortUsername.equals(username)) {
                    mentionedUser = mention;
                    break;
                }
            }
        }

        if (mentionedUser != null) {
            service.getUser(mentionedUser.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<MastodonAccount>() {
                                   @Override
                                   public void call(MastodonAccount user) {
                                       tootView.expandUser(user);
                                   }
                               },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e("Open profile: ", throwable.getMessage());
                                }
                            });
        }
    }
}
