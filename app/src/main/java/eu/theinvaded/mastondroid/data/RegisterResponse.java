package eu.theinvaded.mastondroid.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alin.rautoiu on 01.02.2017.
 */
public class RegisterResponse {
    @SerializedName("id")
    long id;
    @SerializedName("redirect_uri")
    String redirectUri;
    @SerializedName("client_id")
    String clientId;
    @SerializedName("client_secret")
    String clientSecret;

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientId() {
        return clientId;
    }
}
