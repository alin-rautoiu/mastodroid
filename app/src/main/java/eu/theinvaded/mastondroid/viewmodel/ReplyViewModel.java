package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ScaleXSpan;
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

    public ObservableField<String> tootText = new ObservableField<>("");
    public ObservableField<String> remainingChars  = new ObservableField<>("500");
    public long replyToId;

    public ReplyViewModel(Context context, ReplyViewModelContract.ReplyView replyView) {
        app = MastodroidApplication.create(context);
        service = app.getMastodroidService(replyView.getCredentials());
    }

    private String getRemainingChars(CharSequence s) {
        return s != null
        ? String.valueOf(500 - s.length())
        : "500";
    }

    public void setReplyTo(long statusId, String username) {
        if (StringUtils.isNotNullOrEmpty(username)) {
            tootText.set("@" + username + " ");
        }
        remainingChars.set(getRemainingChars(tootText.get()));
        replyToId = statusId;
    }

    public TextWatcher charCountWatcher(){
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                remainingChars.set(getRemainingChars(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    public void postStatus() {
        if (replyToId == 0) {
            service.postStatus(tootText.get(), false, "private")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Toot>() {
                        @Override
                        public void call(Toot toot) {
                            Log.d(TAG, "");
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e("postTootError: ", throwable.getMessage());
                        }
                    });
        } else {
            service.postStatusReply(tootText.get(), replyToId, false, "private")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Toot>() {
                        @Override
                        public void call(Toot toot) {

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
