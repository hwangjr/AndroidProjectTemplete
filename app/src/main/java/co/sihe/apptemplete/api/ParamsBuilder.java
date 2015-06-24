package co.sihe.apptemplete.api;

import java.util.HashMap;
import java.util.Map;

import co.sihe.apptemplete.Constants;
import co.sihe.apptemplete.LocalConfig;

/**
 * Created by hwangjr on 6/24/15.
 */
public class ParamsBuilder<K, V> {
    private Map<K, V> params = new HashMap<>();

    public Map<K, V> build() {
        return params;
    }

    public ParamsBuilder addCommonParams() {
        params.put((K) Constants.APIPostKey.OS, (V) LocalConfig.getOS());
        params.put((K) Constants.APIPostKey.SESSION, (V) LocalConfig.instance().getSession());
        return this;
    }

    public ParamsBuilder put(K key, V value) {
        params.put(key, value);
        return this;
    }
}
