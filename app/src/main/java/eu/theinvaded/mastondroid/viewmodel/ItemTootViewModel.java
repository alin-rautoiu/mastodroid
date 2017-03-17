package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Pattern;
import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.utils.Emojione;
import eu.theinvaded.mastondroid.utils.StringUtils;
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
    public ObservableBoolean hasContentWarning;
    public ObservableBoolean showContent;

    public ObservableInt statusTypeVisible;
    public ObservableInt statusTypeBoost;
    public ObservableInt statusTypeFavorite;
    public ObservableInt statusTypeFollow;
    public ObservableInt statusTypeMention;

    public String spoilerText;

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
        statusTypeMention = new ObservableInt(View.GONE);

        hasContentWarning = new ObservableBoolean(false);
        showContent = new ObservableBoolean(!hasContentWarning.get());

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
        final String displayName;
        if (toot.reblog == null) {
            displayName = Emojione.shortnameToUnicode(toot.account.displayName, false);
            return displayName;
        } else {
            displayName = Emojione.shortnameToUnicode(toot.reblog.account.displayName, false);
            return displayName;
        }
    }

    public String getStatusTypeMessage() {
        String status = "";
        statusTypeFavorite.set(View.GONE);
        statusTypeBoost.set(View.GONE);
        statusTypeFollow.set(View.GONE);
        statusTypeMention.set(View.GONE);

        if (toot.statusType == null)
            return status;

        switch (toot.statusType) {
            case Boost:
                statusTypeVisible.set(View.VISIBLE);
                statusTypeBoost.set(View.VISIBLE);
                status = getStatusName() + " boosted";
                break;
            case Favorite:
                statusTypeVisible.set(View.VISIBLE);
                statusTypeFavorite.set(View.VISIBLE);
                status = getStatusName() + " favourited your status";
                break;
            case Follow:
                statusTypeVisible.set(View.VISIBLE);
                statusTypeFollow.set(View.VISIBLE);
                status = getStatusName() + " followed you";
                break;
            case Mention:
                statusTypeVisible.set(View.VISIBLE);
                statusTypeMention.set(View.VISIBLE);
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
        //Placeholder here till i figure out things
        //Resets/refreshes toot status on timeline update
        if (toot.reblog != null) {
            isFavorited.set(toot.reblog.favorited);
            reblogged.set(toot.reblog.reblogged);
            spoilerText = toot.reblog.spoiler_text;
        } else {
            isFavorited.set(toot.favorited);
            reblogged.set(toot.reblogged);
            spoilerText = toot.spoiler_text;
        }

        if (!TextUtils.isEmpty(spoilerText)) {
            hasContentWarning.set(true);
            showContent.set(false);
        } else {
            hasContentWarning.set(false);
            showContent.set(true);
        }

        if (toot.statusType == StatusType.Boost) {
            content = Emojione.shortnameToUnicode(toot.reblog.content, false);
            statusTypeVisible.set(View.VISIBLE);
        } else if (toot.statusType != StatusType.Follow) {
            content = Emojione.shortnameToUnicode(toot.content, false);
            ;
            statusTypeVisible.set(View.GONE);
        } else {
            content = "";
            statusTypeVisible.set(View.GONE);
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
                    } else if (isTagLink(spanString)) {
                        tootView.startSearchActivity(spanString.substring(1));
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

                boolean isTagLink(String linkText) {
                    return linkText.charAt(0) == '#';
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

    public void toggleContent() {
        showContent.set(!showContent.get());
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
