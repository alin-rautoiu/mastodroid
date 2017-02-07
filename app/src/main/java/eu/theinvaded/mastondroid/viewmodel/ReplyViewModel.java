package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.utils.StringUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by alin on 27.12.2016.
 */

public class ReplyViewModel extends BaseObservable implements ReplyViewModelContract.ViewModel {
    private final MastodroidApplication app;
    private final MastodroidService service;
    private ReplyViewModelContract.ReplyView replyView;

    public ObservableField<String> tootText = new ObservableField<>("");
    public ObservableField<String> spoilerText = new ObservableField<>("");
    public ObservableInt remainingChars = new ObservableInt(500);
    public ObservableBoolean isPrivate = new ObservableBoolean(false);
    public ObservableBoolean notDisplayPublic = new ObservableBoolean(false);
    public ObservableBoolean showContentWarning = new ObservableBoolean(false);
    public ObservableBoolean showLoadingIndicator = new ObservableBoolean(false);
    public ObservableBoolean enabledTootButton = new ObservableBoolean(true);

    public long replyToId;

    public ReplyViewModel(Context context, ReplyViewModelContract.ReplyView replyView) {
        app = MastodroidApplication.create(context);
        service = app.getMastodroidService(replyView.getCredentials());

        tootText.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                remainingChars.set(getRemainingCharsCount());
            }
        });

        spoilerText.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                remainingChars.set(getRemainingCharsCount());
            }
        });

        this.replyView = replyView;
    }

    private int getRemainingCharsCount() {
        int remainingChars = 500 - tootText.get().length() - spoilerText.get().length();
        enabledTootButton.set(remainingChars >= 0);

        return remainingChars;
    }

    public void setReplyTo(long statusId, String username) {
        if (StringUtils.isNotNullOrEmpty(username)) {
            tootText.set("@" + username + " ");
        }
        remainingChars.set(getRemainingCharsCount());
        replyToId = statusId;
    }

    public void postStatus() {
        showLoadingIndicator.set(true);
        enabledTootButton.set(false);

        String statusVisibility = isPrivate.get()
                ? "private"
                : notDisplayPublic.get()
                ? "unlisted"
                : "public";
        String spoilerTextTemp = spoilerText.get();
        if (!showContentWarning.get()) {
            spoilerTextTemp = "";
        }
        rx.Observable<Toot> postStatus;
        if (replyToId == 0) {
            postStatus = service.postStatus(tootText.get(), spoilerTextTemp, false, statusVisibility);
        } else {
            postStatus = service.postStatusReply(tootText.get(), spoilerTextTemp, replyToId, false, statusVisibility);
        }

        postStatus.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Toot>() {
                    @Override
                    public void call(Toot toot) {
                        replyView.goToParent();
                        showLoadingIndicator.set(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("postTootError: ", throwable.getMessage());
                        showLoadingIndicator.set(false);
                        enabledTootButton.set(true);
                        replyView.showError();
                    }
                });
    }

    @Override
    public void destroy() {
    }
}
