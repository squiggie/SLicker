package slicker.com.slicker.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.R;

public class FullscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        final ImageView fullScreenImage = (ImageView) findViewById(R.id.fullscreen_view);
        Bundle bundle = getIntent().getExtras();
        String farm = String.valueOf(bundle.getInt("farm", 0));
        String server = String.valueOf(bundle.getInt("server",0));
        String id = bundle.getString("id", "");
        String secret = bundle.getString("secret", "");
        String url = String.format(MyConstants.IMAGE_URL, farm, server, id, secret, "z");
        Glide
                .with(this)
                .load(url)
                .into(fullScreenImage);
    }
}
