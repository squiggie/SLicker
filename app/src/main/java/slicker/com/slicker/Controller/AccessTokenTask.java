package slicker.com.slicker.Controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth10aService;

import slicker.com.slicker.R;

/**
 * Created by squiggie on 2/23/16.
 */
public class AccessTokenTask extends AsyncTask<String, Integer, Token> {

    private Context mContext;
    private MyInterfaces.OnAccessTokenTaskCompleted mListener;
    private ProgressDialog mProgressDialog;


    public AccessTokenTask(Context context, MyInterfaces.OnAccessTokenTaskCompleted listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContext,"", "Completing request...");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                AccessTokenTask.this.cancel(true);
            }
        });
    }

    @Override
    protected Token doInBackground(String... params) {
        try {
            OAuth10aService service = new ServiceBuilder()
                    .apiKey(params[0])
                    .apiSecret(params[1])
                    .build(FlickrApi.instance());
            Token requestToken = new Token(params[2], params[3]);
            Verifier verifier = new Verifier(params[4]);
            Token accessToken = service.getAccessToken(requestToken, verifier);
            return accessToken;
        } catch (Exception e){
            return new Token("error","error");
        }
    }

    @Override
    protected void onPostExecute(Token accessToken) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (accessToken.getToken().contains("error")){
            Toast.makeText(mContext, R.string.basic_error, Toast.LENGTH_LONG).show();
        } else {
            mListener.onAccessTokenTaskCompleted(accessToken);
        }
    }
}

