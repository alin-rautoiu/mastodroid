package eu.theinvaded.mastondroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alin on 09.12.2016.
 */

public class MediaAttachments implements Parcelable {

    @SerializedName("url")
    public String url;
    @SerializedName("preview_url")
    public String previewUrl;
    @SerializedName("type")
    public Type type;

    public enum Type {
        @SerializedName("image")
        IMAGE,
        @SerializedName("video")
        VIDEO
    }

    protected MediaAttachments(Parcel in) {
    }

    public static final Creator<MediaAttachments> CREATOR = new Creator<MediaAttachments>() {
        @Override
        public MediaAttachments createFromParcel(Parcel in) {
            return new MediaAttachments(in);
        }

        @Override
        public MediaAttachments[] newArray(int size) {
            return new MediaAttachments[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
