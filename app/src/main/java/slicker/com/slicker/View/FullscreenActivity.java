package slicker.com.slicker.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.R;

public class FullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        ImageView fullScreenImage = (ImageView) findViewById(R.id.imageFullScreen);

        Bundle bundle = getIntent().getExtras();
        String farm = String.valueOf(bundle.getInt("farm", 0));
        String server = String.valueOf(bundle.getInt("server", 0));
        String id = bundle.getString("id", "");
        String secret = bundle.getString("secret", "");
        String owner = bundle.getString("owner", "");
        String url = String.format(MyConstants.IMAGE_URL, farm, server, id, secret, "z");


        Glide
                .with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(new ColorDrawable(Color.GRAY))
                .crossFade()
                .into(fullScreenImage);
    }
}
