package httpclient;

import okhttp3.*;

import java.util.function.Consumer;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        call(request, callback);
    }

    public static void runAsync(String finalUrl, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();

        call(request, callback);
    }

    public static void runAsync(String finalUrl, String bodyStr, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(RequestBody.create(bodyStr.getBytes()))
                .build();

        call(request, callback);
    }

    private static void call(Request request, Callback callback) {
        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
