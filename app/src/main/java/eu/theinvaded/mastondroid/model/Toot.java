package eu.theinvaded.mastondroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alin on 09.12.2016.
 */

public class Toot implements Parcelable {

    @SerializedName("id")
    public long id;
    @SerializedName("created_at")
    public Date createdAt;
    @SerializedName("sensitive")
    public boolean sensitive;
    @SerializedName("account")
    public MastodonAccount account;
    @SerializedName("media_attachments")
    public List<MediaAttachments> mediaAttachments;
    @SerializedName("mentions")
    public List<MastodonAccount> mentions;
    @SerializedName("tags")
    public List<Tag> tags;
    @SerializedName("url")
    public String url;
    @SerializedName("content")
    public String content;
    @SerializedName("reblogs_count")
    public int reblogs_count;
    @SerializedName("favorites_count")
    public int favorites_count;
    @SerializedName("favourited")
    public boolean favorited;
    @SerializedName("reblog")
    public Toot reblog;
    @SerializedName("reblogged")
    public boolean reblogged;

    public StatusType statusType;
    public boolean isNotification;
    public MastodonAccount notifiedAccound;
    public boolean isHiglighted;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeByte(this.sensitive ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.account, flags);
        dest.writeList(this.mediaAttachments);
        dest.writeList(this.mentions);
        dest.writeList(this.tags);
        dest.writeString(this.url);
        dest.writeString(this.content);
        dest.writeInt(this.reblogs_count);
        dest.writeInt(this.favorites_count);
        dest.writeByte(this.favorited ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.reblog, flags);
        dest.writeByte(this.reblogged ? (byte) 1 : (byte) 0);
        dest.writeInt(this.statusType == null ? -1 : this.statusType.ordinal());
        dest.writeByte(this.isNotification ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.notifiedAccound, flags);
        dest.writeByte(this.isHiglighted ? (byte) 1 : (byte) 0);
    }

    public Toot() {
    }

    protected Toot(Parcel in) {
        this.id = in.readLong();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.sensitive = in.readByte() != 0;
        this.account = in.readParcelable(MastodonAccount.class.getClassLoader());
        this.mediaAttachments = new ArrayList<>();
        in.readList(this.mediaAttachments, MediaAttachments.class.getClassLoader());
        this.mentions = new ArrayList<>();
        in.readList(this.mentions, MastodonAccount.class.getClassLoader());
        this.tags = new ArrayList<Tag>();
        in.readList(this.tags, Tag.class.getClassLoader());
        this.url = in.readString();
        this.content = in.readString();
        this.reblogs_count = in.readInt();
        this.favorites_count = in.readInt();
        this.favorited = in.readByte() != 0;
        this.reblog = in.readParcelable(Toot.class.getClassLoader());
        this.reblogged = in.readByte() != 0;
        int tmpStatusType = in.readInt();
        this.statusType = tmpStatusType == -1 ? null : StatusType.values()[tmpStatusType];
        this.isNotification = in.readByte() != 0;
        this.notifiedAccound = in.readParcelable(MastodonAccount.class.getClassLoader());
        this.isHiglighted = in.readByte() != 0;
    }

    public static final Creator<Toot> CREATOR = new Creator<Toot>() {
        @Override
        public Toot createFromParcel(Parcel source) {
            return new Toot(source);
        }

        @Override
        public Toot[] newArray(int size) {
            return new Toot[size];
        }
    };
}
