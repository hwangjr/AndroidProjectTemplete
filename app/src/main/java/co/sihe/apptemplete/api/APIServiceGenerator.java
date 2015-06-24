package co.sihe.apptemplete.api;

import android.util.Base64;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import co.sihe.apptemplete.api.entity.AccessToken;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by hwangjr on 6/19/15.
 */
public class APIServiceGenerator {

    private APIServiceGenerator() {
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private static RestAdapter.Builder getBuilder(String baseUrl) {
        return new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(baseUrl).setConverter(new GsonConverter(getGson())).setClient(new OkClient(new OkHttpClient()));
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        RestAdapter adapter = getBuilder(baseUrl).build();
        return adapter.create(serviceClass);
    }

    // Token Authentication
    public static <S> S createService(Class<S> serviceClass, String baseUrl, final String token) {
        RestAdapter.Builder builder = getBuilder(baseUrl);
        if (token != null) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Accept", "application/json");
                    request.addHeader("Authorization", token);
                }
            });
        }

        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }

    // Basic Authentication
    public static <S> S createService(Class<S> serviceClass, String baseUrl, String username, String password) {
        RestAdapter.Builder builder = getBuilder(baseUrl);
        if (username != null && password != null) {
            // concatenate username and password with colon for authentication
            final String credentials = username + ":" + password;
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    // create Base64 encodet string
                    String string = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    request.addHeader("Authorization", string);
                    request.addHeader("Accept", "application/json");
                }
            });
        }

        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }

    // OAuth Authentication
    public static <S> S createService(Class<S> serviceClass, String baseUrl, final AccessToken accessToken) {
        // set endpoint url
        RestAdapter.Builder builder = getBuilder(baseUrl);
        if (accessToken != null) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Accept", "application/json");
                    request.addHeader("Authorization", accessToken.getTokenType() + " " + accessToken.getAccessToken());
                }
            });
        }

        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }

}
