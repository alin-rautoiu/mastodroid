package eu.theinvaded.mastondroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alin on 30.12.2016.
 */

public class MastodonThread {
    @SerializedName("ancestors")
    public List<Toot> ancestors;
    @SerializedName("descendants")
    public List<Toot> descendants;
}
