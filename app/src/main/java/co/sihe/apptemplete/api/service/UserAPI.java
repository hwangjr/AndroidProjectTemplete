package co.sihe.apptemplete.api.service;

import java.util.Map;

import co.sihe.apptemplete.api.entity.BaseResponse;
import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.POST;

/**
 * Created by hwangjr on 6/23/15.
 */
public interface UserAPI {
    @POST("/")
    void login(@FieldMap Map<String, String> params, Callback<BaseResponse> callback);
}
