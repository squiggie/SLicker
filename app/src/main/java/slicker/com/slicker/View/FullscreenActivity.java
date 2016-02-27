package slicker.com.slicker.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import slicker.com.slicker.Controller.API.VolleySingleton;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.R;

public class FullscreenActivity extends android.support.v4.app.Fragment {

    public static FullscreenActivity newInstance() {
        FullscreenActivity fragment = new FullscreenActivity();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.activity_fullscreen, container, false);
        final NetworkImageView fullScreenImage = (NetworkImageView) rootView.findViewById(R.id.fullscreen_view);

        Bundle bundle = getArguments();
        String farm = String.valueOf(bundle.getInt("farm", 0));
        String server = String.valueOf(bundle.getInt("server", 0));
        String id = bundle.getString("id", "");
        String secret = bundle.getString("secret", "");
        String owner = bundle.getString("owner", "");

        String url = String.format(MyConstants.IMAGE_URL, farm, server, id, secret, "z");

        ImageLoader loader = VolleySingleton.getInstance().getImageLoader();
        fullScreenImage.setImageUrl(url, loader);
        return rootView;
    }
}
