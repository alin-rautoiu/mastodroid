package eu.theinvaded.mastondroid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alin on 09.12.2016.
 */

public class MastodonAccount {
    @SerializedName("id")
    public long id;
    @SerializedName("username")
    public String username;
    @SerializedName("acct")
    public String acct;
    @SerializedName("display_name")
    public String displayName;
    @SerializedName("note")
    public String note;
    @SerializedName("url")
    public String url;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("header")
    public String header;
    @SerializedName("followers_count")
    public int followersCount;
    @SerializedName("following_count")
    public int followingCount;
    @SerializedName("statuses_count")
    public int statuses_count;
}
