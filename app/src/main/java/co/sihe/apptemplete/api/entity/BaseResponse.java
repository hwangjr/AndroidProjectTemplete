package co.sihe.apptemplete.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hwangjr on 6/19/15.
 */
public class BaseResponse<K> {
    @Expose
    @SerializedName("code")
    public String code;
    @Expose
    @SerializedName("session")
    public String session;
    @Expose
    @SerializedName("data")
    public K data;
}
