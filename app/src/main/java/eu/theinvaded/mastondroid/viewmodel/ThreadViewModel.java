package eu.theinvaded.mastondroid.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.util.Log;

import eu.theinvaded.mastondroid.MastodroidApplication;
import eu.theinvaded.mastondroid.data.MastodroidService;
import eu.theinvaded.mastondroid.model.MastodonThread;
import eu.theinvaded.mastondroid.model.Toot;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by alin on 30.12.2016.
 */

public class ThreadViewModel extends BaseObservable implements ThreadViewModelContract.ViewModel {

    private final MastodroidApplication app;
    private final MastodroidService service;
    private final ThreadViewModelContract.ThreadView threadView;

    public ThreadViewModel(Context context, ThreadViewModelContract.ThreadView threadView) {
        app = MastodroidApplication.create(context);
        service = app.getMastodroidService(threadView.getCredentials());

        this.threadView = threadView;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setToot(long tootId) {
        service.getThread(tootId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<MastodonThread>() {
                               @Override
                               public void call(MastodonThread mastodonThread) {

                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e("Get Thread", "call: ", throwable);
                            }
                        });
    }
}
