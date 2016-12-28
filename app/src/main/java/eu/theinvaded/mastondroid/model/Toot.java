package eu.theinvaded.mastondroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by alin on 09.12.2016.
 */

public class Toot {
    @SerializedName("id")
    public long id;
    @SerializedName("created_at")
    public Date createdAt;
    @SerializedName("sensitive")
    public boolean sensitive;
    @SerializedName("account")
    public MastodonAccount account;
    @SerializedName("mediat_attachments")
    public MediaAttachments mediaAttachments;
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
    @SerializedName("favorited")
    public boolean favorited;
    @SerializedName("reblog")
    public Toot reblog;
    @SerializedName("reblogged")
    public boolean reblogged;

    public StatusType statusType;
    public boolean isNotification;
    public MastodonAccount notifiedAccound;
}
