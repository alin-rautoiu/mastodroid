package eu.theinvaded.mastondroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alin on 09.12.2016.
 */

public class MastodonAccount implements Parcelable {
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

    protected MastodonAccount(Parcel in) {
        id = in.readLong();
        username = in.readString();
        acct = in.readString();
        displayName = in.readString();
        note = in.readString();
        url = in.readString();
        avatar = in.readString();
        header = in.readString();
        followersCount = in.readInt();
        followingCount = in.readInt();
        statuses_count = in.readInt();
    }

    public static final Creator<MastodonAccount> CREATOR = new Creator<MastodonAccount>() {
        @Override
        public MastodonAccount createFromParcel(Parcel in) {
            return new MastodonAccount(in);
        }

        @Override
        public MastodonAccount[] newArray(int size) {
            return new MastodonAccount[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeString(acct);
        dest.writeString(displayName);
        dest.writeString(note);
        dest.writeString(url);
        dest.writeString(avatar);
        dest.writeString(header);
        dest.writeInt(followersCount);
        dest.writeInt(followingCount);
        dest.writeInt(statuses_count);
    }
}
