package eu.theinvaded.mastondroid.data;

import java.util.List;

import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.MastodonThread;
import eu.theinvaded.mastondroid.model.Notification;
import eu.theinvaded.mastondroid.model.Relationship;
import eu.theinvaded.mastondroid.model.Token;
import eu.theinvaded.mastondroid.model.Toot;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
                             @Field("client_secret") String clientSecret,
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

    @GET("api/v1/timelines/tag/{hashtag}")
    Observable<List<Toot>> getHashtagTimelineFromPast(@Path("hashtag") String hashtag, @Query("max_id") long maxId);

    @GET("api/v1/timelines/tag/{hashtag}")
    Observable<List<Toot>> getHashtagTimelineUpdate(@Path("hashtag") String hashtag, @Query("since_id") long sinceId);

    @GET("api/v1/timelines/tag/{hashtag}")
    Observable<List<Toot>> getHashtagTimeline(@Path("hashtag") String hashtag);

    @POST("api/v1/statuses/{id}/reblog")
    Observable<Toot> reblogStatus(@Path("id") long statusId);

    @POST("api/v1/statuses/{id}/unreblog")
    Observable<Toot> unreblogStatus(@Path("id") long statusId);

    @POST("api/v1/statuses/{id}/favourite")
    Observable<Toot> favoriteStatus(@Path("id") long statusId);

    @POST("api/v1/statuses/{id}/unfavourite")
    Observable<Toot> unfavoriteStatus(@Path("id") long statusId);

    @GET("api/v1/statuses/{id}/context")
    Observable<MastodonThread> getThread(@Path("id") long statusId);

    @GET("api/v1/statuses/{id}/")
    Observable<Toot> getStatus(@Path("id") Toot statusId);

    @GET("api/v1/accounts/verify_credentials")
    Observable<MastodonAccount> verifyCredentials();

    @GET("api/v1/accounts/{id}")
    Observable<MastodonAccount> getUser(@Path("id") long id);

    @GET("api/v1/accounts/{id}/statuses")
    Observable<List<Toot>> getStatusesForUser(@Path("id") long id);

    @GET("api/v1/accounts/{id}/statuses")
    Observable<List<Toot>> getStatusesForUserFromPast(@Path("id") long id, @Query("max_id") long maxId);

    @GET("api/v1/accounts/relationships")
    Observable<List<Relationship>> relationships(@Query("id[]") List<Long> id);

    @POST("api/v1/accounts/{id}/follow")
    Observable<Relationship> followUser(@Path("id") long id);

    @GET("api/v1/accounts/{id}/following")
    Observable<List<MastodonAccount>> getFollowing(@Path("id") long id);

    @GET("api/v1/accounts/{id}/following")
    Observable<List<MastodonAccount>> getFollowingNext(@Path("id") long id, @Query("max_id") long maxId);

    @GET("api/v1/accounts/{id}/followers")
    Observable<List<MastodonAccount>> getFollowers(@Path("id") long id);

    @GET("api/v1/accounts/{id}/followers")
    Observable<List<MastodonAccount>> getFollowersNext(@Path("id") long id, @Query("max_id") long maxId);

    @POST("api/v1/accounts/{id}/unfollow")
    Observable<Relationship> unfollowUser(@Path("id") long id);

    @GET("api/v1/accounts/search")
    Observable<List<MastodonAccount>> searchUsers(@Query("q") String searchTerm, @Query("limit") int maxUsers);

    @GET("api/v1/accounts/search")
    Observable<List<MastodonAccount>> searchUsersNext(@Query("q") String searchTerm, @Query("limit") int maxUsers, @Query("since_id") long maxId);

    @GET("api/v1/notifications")
    Observable<List<Notification>> getNotifications();

    @FormUrlEncoded
    @POST("/api/v1/statuses")
    Observable<Toot> postStatusReplyWithMedia(@Field("status") String toot,
                                              @Field("spoiler_text") String spoilerText,
                                              @Field("in_reply_to_id") long replyToId,
                                              @Field("media_id") long mediaId,
                                              @Field("sensitive") boolean sensitive,
                                              @Field("visibility") String visibility);

    @FormUrlEncoded
    @POST("/api/v1/statuses")
    Observable<Toot> postStatusReply(@Field("status") String toot,
                                     @Field("spoiler_text") String spoilerText,
                                     @Field("in_reply_to_id") long replyToId,
                                     @Field("sensitive") boolean sensitive,
                                     @Field("visibility") String visibility);

    @FormUrlEncoded
    @POST("/api/v1/statuses")
    Observable<Toot> postStatusWithMedia(@Field("status") String toot,
                                         @Field("spoiler_text") String spoilerText,
                                         @Field("media_id") long mediaId,
                                         @Field("sensitive") boolean sensitive,
                                         @Field("visibility") String visibility);

    @FormUrlEncoded
    @POST("/api/v1/statuses")
    Observable<Toot> postStatus(@Field("status") String toot,
                                @Field("spoiler_text") String spoilerText,
                                @Field("sensitive") boolean sensitive,
                                @Field("visibility") String visibility);
}
