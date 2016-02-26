package slicker.com.slicker.Controller;

/**
 * Created by squiggie on 2/24/16.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
        queue = Volley.newRequestQueue(context);
    }

    public RequestQueue getQueue() {
        return queue;
    }

    public interface StringCallback {
        public void onDownloadReady(String result);
        public void onErrorResponse(VolleyError error);
    }

//    public interface DiskCallback {
//        public void onDownloadReady(String path);
//    }

    public void download(String url, final StringCallback cb) {
        queue.add(new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                cb.onDownloadReady(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.onErrorResponse(error);
            }
        }));
    }

    /*public Request download(String url, final File out, final DiskCallback cb) {
        return queue.add(new Request<String>(Request.Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                OutputStream os = null;
                try {
                    os = new BufferedOutputStream(new FileOutputStream(out));
                    os.write(response.data);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) { }
                    }
                }
                return Response.success(out.getAbsolutePath(), getCacheEntry());
            }
            @Override
            protected void deliverResponse(String response) {
                cb.onDownloadReady(response);
            }
        });
    }*/
}
