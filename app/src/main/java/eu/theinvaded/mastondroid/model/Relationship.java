package eu.theinvaded.mastondroid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alin on 07.01.2017.
 */

public class Relationship {
    @SerializedName("id")
    long id;
    @SerializedName("following")
    boolean following;
    @SerializedName("followed_by")
    boolean followedBy;
    @SerializedName("blocking")
    boolean blocking;
    @SerializedName("requested")
    boolean requested;


    public boolean isFollowing() {
        return following;
    }
}
