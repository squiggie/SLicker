package slicker.com.slicker.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth10aService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import slicker.com.slicker.Controller.API.Api;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.OAuth;
import slicker.com.slicker.Model.OAuthToken;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.Model.User;
import slicker.com.slicker.R;

public class LoginActivity extends AppCompatActivity{

    @Bind(R.id.login) Button btnLogin;
    @Bind(R.id.slicker) TextView slicker;
    @Bind(R.id.webView) WebView webView;
    @Bind(R.id.imageLogo) ImageView logo;
    @Bind(R.id.imageLoginBackground) ImageView imageLoginBackground;

    private SharedPreferences sp;
    private List<Photo> mPhotos = new ArrayList<>();

    private static final String OAUTH_CALLBACK_URI = "slicker://";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sp = getSharedPreferences(MyConstants.SP_KEY,MODE_PRIVATE);

        OAuth oauth = getOAuthToken();
        if (oauth != null) {
            if (oauth.getUser() != null){
                startSlicker();
            }
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(MyConstants.CALLBACK_SCHEME)) {
                    Uri uri = Uri.parse(url);
                    toggleVisibility();
                    completeOAuthVerify(uri);
                    return true;
                }

                return false;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();

        getPhotos();


    }

    @OnClick(R.id.login)
    public void login(){
        //RequestTokenTask task = new RequestTokenTask(this,this,this);
        //task.execute(MyConstants.API_KEY, MyConstants.API_SEC);

        Observable<String> observable = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    OAuth10aService service = new ServiceBuilder()
                            .apiKey(MyConstants.API_KEY)
                            .apiSecret(MyConstants.API_SEC)
                            .callback(OAUTH_CALLBACK_URI)
                            .build(FlickrApi.instance());
                    Token requestToken = service.getRequestToken();
                    saveOAuthToken(null,null,requestToken.getToken(),requestToken.getSecret());
                    return Observable.just(service.getAuthorizationUrl(requestToken));
                }
                catch (Exception e) {
                    return Observable.just("error:" + e.getMessage());
                }
            }
        });

        Subscriber<String> requestSubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                toggleVisibility();
                webView.loadUrl(s);
            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(requestSubscriber);

    }

    public OAuth getOAuthToken() {
        //Restore preferences
        String oauthTokenString = sp.getString(MyConstants.KEY_OAUTH_TOKEN, null);
        String tokenSecret = sp.getString(MyConstants.KEY_TOKEN_SECRET, null);
        if (oauthTokenString == null && tokenSecret == null) {
            return null;
        }
        OAuth oauth = new OAuth();
        String userName = sp.getString(MyConstants.KEY_USER_NAME, null);
        String userId = sp.getString(MyConstants.KEY_USER_ID, null);
        if (userId != null) {
            User user = new User();
            user.setUsername(userName);
            user.setId(userId);
            oauth.setUser(user);
        }
        OAuthToken oauthToken = new OAuthToken();
        oauth.setToken(oauthToken);
        oauthToken.setOauthToken(oauthTokenString);
        oauthToken.setOauthTokenSecret(tokenSecret);
        return oauth;
    }

    private void startSlicker(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
        SharedPreferences.Editor editor = sp.edit();
        if (token != null) { editor.putString(MyConstants.KEY_OAUTH_TOKEN, token); }
        if (tokenSecret != null){editor.putString(MyConstants.KEY_TOKEN_SECRET, tokenSecret);}
        if (userName != null) {editor.putString(MyConstants.KEY_USER_NAME, userName);}
        if (userId != null) {editor.putString(MyConstants.KEY_USER_ID, userId);}
        editor.commit();
    }

    private void completeOAuthVerify(Uri uri){
        if (uri != null){

            String query = uri.getQuery();
            String[] data = query.split("&");

            if (data != null && data.length == 2) {
                final String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
                final String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1);
                saveOAuthToken(null,null,oauthToken,null);
                final OAuth oauth = getOAuthToken();
                if (oauth != null && oauth.getToken() != null && oauth.getToken().getOauthTokenSecret() != null) {
                    //AccessTokenTask task = new AccessTokenTask(this,this);
                    //task.execute(MyConstants.API_KEY, MyConstants.API_SEC,oauthToken,oauth.getToken().getOauthTokenSecret(),oauthVerifier);

                    Observable<Token> observable = Observable.defer(new Func0<Observable<Token>>() {
                        @Override
                        public Observable<Token> call() {
                            try {
                                OAuth10aService service = new ServiceBuilder()
                                        .apiKey(MyConstants.API_KEY)
                                        .apiSecret(MyConstants.API_SEC)
                                        .build(FlickrApi.instance());
                                Token requestToken = new Token(oauthToken, oauth.getToken().getOauthTokenSecret());
                                Verifier verifier = new Verifier(oauthVerifier);
                                Token accessToken = service.getAccessToken(requestToken, verifier);
                                saveOAuthToken(null,null,accessToken.getToken(),accessToken.getSecret());
                                return Observable.just(accessToken);
                            } catch (Exception e){
                                return Observable.just(new Token("error","error"));
                            }
                        }
                    });

                    Subscriber<Token> accessSubscriber = new Subscriber<Token>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Token token) {
                            if (token.getToken().contains("error")){
                                Toast.makeText(getApplicationContext(), R.string.basic_error, Toast.LENGTH_LONG).show();
                            } else {
                                getUserInfo(token);
                            }
                        }
                    };

                    observable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(accessSubscriber);
                }
            }
        }
    }

    private void toggleVisibility(){
        if (btnLogin.getVisibility() == View.GONE){
            btnLogin.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setVisibility(View.GONE);
        }

        if (webView.getVisibility() == View.GONE){
            webView.setVisibility(View.VISIBLE);
        } else {
            webView.setVisibility(View.GONE);
        }

        if (slicker.getVisibility() == View.GONE){
            slicker.setVisibility(View.VISIBLE);
        } else {
            slicker.setVisibility(View.GONE);
        }
        if (logo.getVisibility() == View.GONE){
            logo.setVisibility(View.VISIBLE);
        } else {
            logo.setVisibility(View.GONE);
        }
    }

    private void getPhotos() {
        String interestingURL = String.format(MyConstants.BASE_FLICKR_URL, MyConstants.FLICKR_METHOD_INTERESTING, MyConstants.API_KEY + "&per_page=" + String.valueOf(300));
        Api.get(this).getJSON(interestingURL, new Api.JSONCallback() {
            @Override
            public void onGetJSONComplete(JSONObject json) {
                try {
                    JSONObject photos = json.getJSONObject("photos");
                    JSONArray items = photos.getJSONArray("photo");
                    for (int i = 0; i < items.length(); i++){
                        Photo photo = new Photo(items.getJSONObject(i));
                        mPhotos.add(photo);
                    }
                    animate(0);
                } catch (JSONException e) {
                }
            }
            @Override
            public void onGetJSONComplete(VolleyError error) {
            }
        });
    }

    private void animate(final int imageIndex) {
        imageLoginBackground.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        Photo photo = mPhotos.get(imageIndex);
        String size = "c";
        String url = String.format(MyConstants.IMAGE_URL, photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret(), size);
        Glide.with(this).load(url).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                int fadeInDuration = 5000; // Configure time values here
                int timeBetween = 3000;
                int fadeOutDuration = 5000;

                imageLoginBackground.setImageDrawable(resource);
                Animation fadeIn = new AlphaAnimation(0.00f,.9f);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(fadeInDuration);

                Animation fadeOut = new AlphaAnimation(.9f,0.00f);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setStartOffset(fadeInDuration + timeBetween);
                fadeOut.setDuration(fadeOutDuration);

                Random random = new Random();
                Float randX = random.nextFloat() * (1.2f - 1f) + 1f;
                Float randY = random.nextFloat() * (1.22f - 1f) + 1f;
                Animation zoom = new ScaleAnimation(1.0f,randX,1.0f,randY);
                zoom.setDuration(fadeInDuration + timeBetween + fadeOutDuration);

                AnimationSet animation = new AnimationSet(false);
                animation.addAnimation(fadeIn);
                animation.addAnimation(fadeOut);
                animation.addAnimation(zoom);
                animation.setRepeatCount(1);

                imageLoginBackground.startAnimation(animation);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (imageIndex  + 1 > mPhotos.size() - 1) {
                            animate(0);  //Calls itself to start the animation all over again in a loop if forever = true
                        }
                        else {
                            animate(imageIndex + 1); //Calls itself until it gets to the end of the array
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    private void getUserInfo(final Token token){

        Observable<String> observable = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                OAuth10aService service = new ServiceBuilder()
                        .apiKey(MyConstants.API_KEY)
                        .apiSecret(MyConstants.API_SEC)
                        .build(FlickrApi.instance());
                OAuthRequest request = new OAuthRequest(Verb.GET, MyConstants.PROTECTED_RESOURCE_URL,service);
                request.addQuerystringParameter("method","flickr.test.login");
                request.addQuerystringParameter("format","json");
                service.signRequest(token, request);
                Response response = request.send();
                return Observable.just(response.getBody());
            }
        });

        Subscriber<String> userSubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                if (s.contains("username")){
                    String json = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
                    try {
                        JSONObject parsedJson = new JSONObject(json);
                        JSONObject user = parsedJson.getJSONObject("user");
                        String id = user.getString("id");
                        JSONObject usernameObject = user.getJSONObject("username");
                        String username = usernameObject.getString("_content");
                        saveOAuthToken(username,id,null,null);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Authorization failed", Toast.LENGTH_LONG).show();
                    }
                    startSlicker();
                } else {
                    Toast.makeText(getApplicationContext(), "Authorization failed", Toast.LENGTH_LONG).show();
                }
            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userSubscriber);
    }
}
