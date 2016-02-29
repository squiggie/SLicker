package slicker.com.slicker.View;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.R;

public class FullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        final ImageView fullScreenImage = (ImageView) findViewById(R.id.imageFullScreen);
        Bundle bundle = getIntent().getExtras();
        String farm = String.valueOf(bundle.getInt("farm", 0));
        String server = String.valueOf(bundle.getInt("server", 0));
        String id = bundle.getString("id", "");
        String secret = bundle.getString("secret", "");
        String owner = bundle.getString("owner", "");
        String url = String.format(MyConstants.IMAGE_URL, farm, server, id, secret, "h");


        Glide
                .with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(new ColorDrawable(Color.GRAY))
                .crossFade()
                .into(new ViewTarget<ImageView, GlideDrawable>(fullScreenImage) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();
                        if (width >= height){
                            //set screen rotation horizontal and lock
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                        } else {
                            //set screen rotation vertical and lock
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                        }
                        fullScreenImage.setImageDrawable(resource);
                    }
                });
    }
}
