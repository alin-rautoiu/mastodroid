package eu.theinvaded.mastondroid.data;

import android.accounts.Account;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;

import java.util.List;

import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.Notification;
import eu.theinvaded.mastondroid.model.Token;
import eu.theinvaded.mastondroid.model.Toot;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by alin on 09.12.2016.
 */

public interface MastodroidService {

    @FormUrlEncoded
    @POST("oauth/token")
    Observable<Token> SignIn(@Field("client_id") String clientId,
                             @Field("client_secrete_here") String clientSecret,
                             @Field("scope") String scope,
                             @Field("grant_type") String grantType,
                             @Field("username") String username,
                             @Field("password") String password);

    @GET("api/v1/timelines/public")
    Observable<List<Toot>> getPublicTimeLine();

    @GET("api/v1/timelines/public")
    Observable<List<Toot>> getPublicTimeLineFromPast(@Query("max_id") long maxId);

    @GET("api/v1/timelines/public")
    Observable<List<Toot>> getPublicTimeLineUpdate(@Query("since_id") long sinceId);

    @GET("api/v1/timelines/home")
    Observable<List<Toot>> getHomeTimeLineFromPast(@Query("max_id") long maxId);

    @GET("api/v1/timelines/home")
    Observable<List<Toot>> getHomeTimeLineUpdate(@Query("since_id") long sinceId);

    @GET("api/v1/timelines/home")
    Observable<List<Toot>> getHomeTimeLine();

    @POST("api/v1/statuses/{id}/reblog")
    Observable<Toot> reblogStatus(@Path("id") long statusId);

    @POST("api/v1/statuses/{id}/unreblog")
    Observable<Toot> unreblogStatus(@Path("id") long statusId);

    @POST("api/v1/statuses/{id}/favourite")
    Observable<Toot> favoriteStatus(@Path("id") long statusId);

    @POST("api/v1/statuses/{id}/unfavourite")
    Observable<Toot> unfavoriteStatus(@Path("id") long statusId);

    @GET("api/v1/accounts/verify_credentials")
    Observable<MastodonAccount> verifyCredentials();

    @GET("api/v1/notifications")
    Observable<List<Notification>> getNotifications();

    @FormUrlEncoded
    @POST("/api/v1/statuses")
    Observable<Toot> postStatusReplyWithMedia(@Field("status") String toot,
                                @Field("in_reply_to_id") long replyToId,
                                @Field("media_id") long mediaId,
                                @Field("sensitive") boolean sensitive,
                                @Field("visibility") String visibility);

    @FormUrlEncoded
    @POST("/api/v1/statuses")
    Observable<Toot> postStatusReply(@Field("status") String toot,
                                     @Field("in_reply_to_id") long replyToId,
                                     @Field("sensitive") boolean sensitive,
                                     @Field("visibility") String visibility);

    @FormUrlEncoded
    @POST("/api/v1/statuses")
    Observable<Toot> postStatusWithMedia(@Field("status") String toot,
                                @Field("media_id") long mediaId,
                                @Field("sensitive") boolean sensitive,
                                @Field("visibility") String visibility);

    @FormUrlEncoded
    @POST("/api/v1/statuses")
    Observable<Toot> postStatus(@Field("status") String toot,
                                @Field("sensitive") boolean sensitive,
                                @Field("visibility") String visibility);
}
