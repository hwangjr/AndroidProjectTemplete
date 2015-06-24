package co.sihe.apptemplete.api.service;

import java.lang.ref.SoftReference;

import co.sihe.apptemplete.Constants;
import co.sihe.apptemplete.api.APIServiceGenerator;
import co.sihe.apptemplete.api.ParamsBuilder;
import co.sihe.apptemplete.api.entity.BaseResponse;
import retrofit.Callback;

/**
 * Created by hwangjr on 6/24/15.
 */
public final class UserService {

    private static SoftReference<UserAPI> sUserServiceRef = new SoftReference<UserAPI>(null);

    private static UserAPI getUserAPI() {
        UserAPI userAPI = sUserServiceRef.get();
        if (userAPI == null) {
            userAPI = APIServiceGenerator.createService(UserAPI.class, Constants.Protocol.SERVER_URL);
            sUserServiceRef = new SoftReference<UserAPI>(userAPI);
        }
        return userAPI;
    }

    public static void login(String tel, String passwd, Callback<BaseResponse> callback) {
        getUserAPI().login(new ParamsBuilder<String, String>().put(Constants.APIPostKey.TEL, tel).put(Constants.APIPostKey.PASSWD, passwd).addCommonParams().build(), callback);
    }
}
