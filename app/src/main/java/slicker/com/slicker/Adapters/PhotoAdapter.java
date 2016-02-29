package slicker.com.slicker.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder>{
    private Context mContext;
    private List<Photo> mPhotos = new ArrayList<>();
    private static MyInterfaces.RecyclerViewClickListener mListener;
    private Realm mRealm;

    public PhotoAdapter(Context context, MyInterfaces.RecyclerViewClickListener listener){
        mContext = context;
        mListener = listener;
        mRealm = Realm.getInstance(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String farm = String.valueOf(mPhotos.get(position).getFarm());
        String server = String.valueOf(mPhotos.get(position).getServer());
        String id = mPhotos.get(position).getId();
        String secret = mPhotos.get(position).getSecret();
        String size = "n";

        //Get and set main Photo
        String url = String.format(MyConstants.IMAGE_URL, farm, server, id, secret, size);
        Glide.with(mContext).load(url).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(new ColorDrawable(Color.GRAY)).crossFade().into(holder.imageViewSquare);

        /*//set Photo title
        holder.tvTitle.setText(mPhotos.get(position).getTitle());

        //set if photo is favorite
        RealmQuery<Photo> query = mRealm.where(Photo.class);
        query.equalTo("id", id);
        Photo results = query.findFirst();
        if ((results != null && results.getIsFavorite() == true) || mPhotos.get(position).getIsFavorite()){
            holder.imageViewFavorite.setImageResource(R.drawable.ic_star);
        } else {
            holder.imageViewFavorite.setImageResource(R.drawable.ic_star_outline);
        }*/
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
        private ImageView imageViewSquare;
        //private CircleImageView buddyIcon;
        //private TextView tvUserNameCard;
        //private TextView tvTitle;
        //private ImageView imageViewFavorite;


        public MyViewHolder(View itemView) {
            super(itemView);
            imageViewSquare = (ImageView) itemView.findViewById(R.id.imageViewSquare);
            //buddyIcon = (CircleImageView) itemView.findViewById(R.id.buddyIconCard);
            //tvUserNameCard = (TextView) itemView.findViewById(R.id.tvUserNameCard);
            //tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            //imageViewFavorite = (ImageView) itemView.findViewById(R.id.imageViewFavorite);

            imageViewSquare.setOnClickListener(this);
            //buddyIcon.setOnClickListener(this);
            //imageViewFavorite.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.recyclerViewMainImageClicked(mPhotos.get(getLayoutPosition()), v);
        }
    }
}
