package eu.theinvaded.mastondroid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alin on 24.12.2016.
 */

public class Notification {
    @SerializedName("id")
    public long id;

    @SerializedName("type")
    public String type;

    @SerializedName("account")
    public MastodonAccount account;

    @SerializedName("status")
    public Toot status;
}
