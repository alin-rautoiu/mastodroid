package eu.theinvaded.mastondroid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alin on 23.12.2016.
 */

public class Token {
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("token_type")
    public String tokenType;
    @SerializedName("scope")
    public String scope;
    @SerializedName("created_at")
    public String date;
}
