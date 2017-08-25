package app.techinshorts.techinshortsapp.utils;

/**
 * Created by sp on 25/8/17.
 */

public class Utility {
    public static final String BASE_URL = "http://192.168.0.106:3000";
    public static String fetchApi(String... args) {
        String url = BASE_URL + "/news.json";
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < args.length; i += 2) {
            params.append(i == 0 ? "" : "&").append(args[i]).append("=").append(args[i + 1]);
        }
        if (params.length() > 0) {
            url = url + "?" + params.toString();
        }
        return url;
    }
}
