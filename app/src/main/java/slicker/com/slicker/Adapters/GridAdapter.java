package slicker.com.slicker.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

/**
 * Created by squiggie on 2/25/16.
 */
public class GridAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<Photo> photos = new ArrayList<>();
    private DrawableRequestBuilder<Photo> fullRequest;

    public GridAdapter(Context context){
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.grid_item,null);
        MyViewHolder holder = new MyViewHolder(itemView);
        holder.imageView = (ImageView)itemView.findViewById(R.id.gridImageView);
        itemView.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder vh = (MyViewHolder) holder;
        String farm = String.valueOf(photos.get(position).getFarm());
        String server = String.valueOf(photos.get(position).getServer());
        String id = photos.get(position).getId();
        String secret = photos.get(position).getSecret();
        String size = "z";

        String url = String.format(MyConstants.IMAGE_URL,farm,server,id,secret,size);
        Glide
                .with(mContext)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(640,640) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        vh.imageView.setImageBitmap(resource);
                    }
                });
        /*loader.loadImage(url, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    vh.imageView.setImageBitmap(loadedImage);
                    Log.d("Adapter", "Downloaded image from " + imageUri);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void add(Photo photo){
        photos.add(photo);
    }
}
