package slicker.com.slicker.Controller.API;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuth10aService;

import slicker.com.slicker.Controller.MyInterfaces;

/**
 * Created by squiggie on 2/23/16.
 */
public class RequestTokenTask extends AsyncTask<String, Integer, String> {

    private static final String OAUTH_CALLBACK_URI = "slicker://";
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private MyInterfaces.OnSaveOAuthRequestToken mListener;
    private MyInterfaces.OnRequestTokenTaskCompleted mListener2;

    public RequestTokenTask(Context context, MyInterfaces.OnSaveOAuthRequestToken listener, MyInterfaces.OnRequestTokenTaskCompleted listener2) {
        super();
        this.mContext = context;
        mListener = listener;
        mListener2 = listener2;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContext,"", "Generating request...");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                RequestTokenTask.this.cancel(true);
            }
        });
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            OAuth10aService service = new ServiceBuilder()
                    .apiKey(params[0])
                    .apiSecret(params[1])
                    .callback(OAUTH_CALLBACK_URI)
                    .build(FlickrApi.instance());
            Token requestToken = service.getRequestToken();
            saveToken(requestToken);
            return service.getAuthorizationUrl(requestToken);
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    private void saveToken(Token requestToken) {
        mListener.onSaveOauthRequestToken(requestToken);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if ((result != null) && !result.startsWith("error")) {
            mListener2.onRequestTokenTaskCompleted(result);
        } else {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }
}
