package slicker.com.slicker.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.DynamicImageView;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class ProfilePhotoAdapter extends RecyclerView.Adapter<ProfilePhotoAdapter.MyViewHolder>{
    private Context mContext;
    private List<Photo> mPhotos = new ArrayList<>();
    private static MyInterfaces.RecyclerViewClickListener mListener;

    public ProfilePhotoAdapter(Context context, MyInterfaces.RecyclerViewClickListener listener){
        mContext = context;
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.profile_grid_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Photo photo = mPhotos.get(position);
        String farm = String.valueOf(photo.getFarm());
        String server = String.valueOf(photo.getServer());
        String id = photo.getId();
        String secret = photo.getSecret();
        String size = "n";

        //Get and set main Photo
        String url = String.format(MyConstants.IMAGE_URL, farm, server, id, secret, size);
        //String thumb = String.format(MyConstants.IMAGE_URL, farm, server, id, secret, "q");
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) holder.profileImageView.getLayoutParams();
        float ratio = (float) photo.getHeight() / (float) photo.getWidth();
        rlp.height = (int) (rlp.width * ratio);
        holder.profileImageView.setLayoutParams(rlp);
        holder.profileImageView.setRatio(ratio);
        //DrawableRequestBuilder<String> thumbnailRequest = Glide.with(mContext).load(thumb);
        Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(new ColorDrawable(Color.GRAY)).crossFade().into(holder.profileImageView);
    }

    public void add(Photo photo){
        mPhotos.add(photo);
    }

    public void clear(){
        mPhotos.clear();
    }

    public Photo getPhoto(int position){
        return mPhotos.get(position);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private DynamicImageView profileImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            profileImageView = (DynamicImageView) itemView.findViewById(R.id.profileImageView);
            profileImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.recyclerViewMainImageClicked(mPhotos.get(getLayoutPosition()), v);
        }
    }
}
