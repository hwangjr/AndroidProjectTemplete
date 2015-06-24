package co.sihe.apptemplete.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hwangjr on 6/19/15.
 */
public class BaseRequest {
    @Expose
    @SerializedName("os")
    public int os = 1;
    @Expose
    @SerializedName("network")
    public int network = 0;
}
