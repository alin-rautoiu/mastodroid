package eu.theinvaded.mastondroid.viewmodel;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.utils.StringUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by alin on 27.12.2016.
 */

public class ReplyViewModel extends BaseObservable implements ReplyViewModelContract.ViewModel {
    private final MastodroidApplication app;
    private final MastodroidService service;
    private ReplyViewModelContract.ReplyView replyView;

    public ObservableField<String> tootText = new ObservableField<>("");
    public ObservableField<String> remainingCharsText = new ObservableField<>("500");
    public ObservableInt remainingChars = new ObservableInt(500);
    public ObservableBoolean isPrivate = new ObservableBoolean(false);
    public ObservableBoolean notDisplayPublic = new ObservableBoolean(false);

    public long replyToId;

    public ReplyViewModel(Context context, ReplyViewModelContract.ReplyView replyView) {
        app = MastodroidApplication.create(context);
        service = app.getMastodroidService(replyView.getCredentials());
        this.replyView = replyView;
    }

    private String getRemainingChars(CharSequence s) {
        remainingChars.set(s != null
                ? 500 - s.length()
                : 500);

        return String.valueOf(remainingChars.get());
    }

    public void setReplyTo(long statusId, String username) {
        if (StringUtils.isNotNullOrEmpty(username)) {
            tootText.set("@" + username + " ");
        }
        remainingCharsText.set(getRemainingChars(tootText.get()));
        replyToId = statusId;
    }

    public TextWatcher charCountWatcher(){
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                remainingCharsText.set(getRemainingChars(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    public void postStatus() {
        String statusVisibility = isPrivate.get()
                ? "private"
                : notDisplayPublic.get()
                    ? "unlisted"
                    : "public";
        if (replyToId == 0) {
            service.postStatus(tootText.get(), false, statusVisibility)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Toot>() {
                        @Override
                        public void call(Toot toot) {
                            Log.d(TAG, "");
                            replyView.goToParent();

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e("postTootError: ", throwable.getMessage());
                        }
                    });
        } else {
            service.postStatusReply(tootText.get(), replyToId, false, statusVisibility)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Toot>() {
                        @Override
                        public void call(Toot toot) {
                            replyView.goToParent();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e("postTootError: ", throwable.getMessage());
                        }
                    });
        }
    }

    @Override
    public void destroy() {
    }
}
