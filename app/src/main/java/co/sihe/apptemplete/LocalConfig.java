package co.sihe.apptemplete;

/**
 * Created by hwangjr on 6/24/15.
 */
public class LocalConfig {

    private static LocalConfig sConfig;

    private String mSession;

    // un synchronized
    public static LocalConfig instance() {
        if(sConfig == null) {
            sConfig = new LocalConfig();
        }
        return sConfig;
    }

    private LocalConfig(){
        // init
    }

    public static String getOS() {
        return "1";
    }

    public String getSession() {
        return mSession;
    }

    public void setSession(String session) {
        this.mSession = session;
    }
}
