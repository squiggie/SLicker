package slicker.com.slicker.Controller.API;

/**
 * Created by squiggie on 2/24/16.
 */

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Downloader {
    private static Downloader DOWNLOADER;
    private final RequestQueue queue;

    public static Downloader get(Context context) {
        if (DOWNLOADER == null) {
            DOWNLOADER = new Downloader(context);
        }
        return DOWNLOADER;
    }

    public Downloader(Context context) {
        queue = VolleySingleton.getInstance().getRequestQueue();
    }

    public RequestQueue getQueue() {
        return VolleySingleton.getInstance().getRequestQueue();
    }

    public interface StringCallback {
        public void onDownloadReady(String result);
        public void onErrorResponse(VolleyError error);
    }


    public void download(String url, final StringCallback cb) {
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                cb.onDownloadReady(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.onErrorResponse(error);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }
}
