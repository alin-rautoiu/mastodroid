package eu.theinvaded.mastondroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alin on 07.01.2017.
 */

public class Relationship implements Parcelable {
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

    public long getId() {
        return id;
    }

    public boolean isFollowedBy() {
        return followedBy;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public boolean isRequested() {
        return requested;
    }

    protected Relationship(Parcel in) {
        id = in.readLong();
        following = in.readByte() != 0;
        followedBy = in.readByte() != 0;
        blocking = in.readByte() != 0;
        requested = in.readByte() != 0;
    }

    public static final Creator<Relationship> CREATOR = new Creator<Relationship>() {
        @Override
        public Relationship createFromParcel(Parcel in) {
            return new Relationship(in);
        }

        @Override
        public Relationship[] newArray(int size) {
            return new Relationship[size];
        }
    };

    public boolean isFollowing() {
        return following;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeByte((byte) (following ? 1 : 0));
        dest.writeByte((byte) (followedBy ? 1 : 0));
        dest.writeByte((byte) (blocking ? 1 : 0));
        dest.writeByte((byte) (requested ? 1 : 0));
    }
}
