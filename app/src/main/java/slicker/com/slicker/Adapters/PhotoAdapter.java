package slicker.com.slicker.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

/**
 * Created by squiggie on 2/25/16.
 */
public class PhotoAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<Photo> mPhotos = new ArrayList<>();

    public PhotoAdapter(Context context){
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
        String farm = String.valueOf(mPhotos.get(position).getFarm());
        String server = String.valueOf(mPhotos.get(position).getServer());
        String id = mPhotos.get(position).getId();
        String secret = mPhotos.get(position).getSecret();
        String size = "b";

        String url = String.format(MyConstants.IMAGE_URL,farm,server,id,secret,size);
        Glide
            .with(mContext)
            .load(url)
            .asBitmap()
            .into(new SimpleTarget<Bitmap>(640, 640) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                    vh.imageView.setImageBitmap(resource);
                    Log.d("Download", "Image " + resource.toString() + " downloaded");
                }
            });
    }


    public void add(Photo photo){
        mPhotos.add(photo);
    }

    public void clear(){
        mPhotos.clear();
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}
