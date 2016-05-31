package slicker.com.slicker.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmQuery;
import slicker.com.slicker.Controller.API.Api;
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
        final Photo photo = mPhotos.get(position);
        final String farm = String.valueOf(photo.getFarm());
        final String server = String.valueOf(photo.getServer());
        final String id = photo.getId();
        final String secret = photo.getSecret();
        String size = "n";

        //Get and set main Photo
        String url = String.format(MyConstants.IMAGE_URL, farm, server, id, secret, size);

        Glide.with(mContext).load(url).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(new ColorDrawable(Color.GRAY)).crossFade().into(holder.imageViewSquare);

        Api.get(mContext).getUserInfo(photo.getOwner(), new Api.UserInfoCallback() {
            @Override
            public void onUserInfoDownloadComplete(String result) {
                try {
                    JSONObject root = new JSONObject(result);
                    JSONObject person = root.getJSONObject("person");
                    JSONObject usernameJObject = person.getJSONObject("username");
                    String username = usernameJObject.getString("_content");
                    String iconFarm = person.getString("iconfarm");
                    String iconServer = person.getString("iconserver");
                    String url = String.format(MyConstants.BUDDY_ICON_URL, iconFarm, iconServer, photo.getOwner());
                    Glide.with(mContext).load(url).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.buddyIcon);
                    holder.tvPhotoViewUsername.setText(username);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onUserInfoDownloadComplete(VolleyError error) {

            }
        });

        //Set if favorite or not
        RealmQuery<Photo> query = mRealm.where(Photo.class);
        query.equalTo("id",photo.getId());
        Photo favorite = query.findFirst();

        if (favorite != null){ //found photo in favorites
            holder.imageFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            holder.imageFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
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
        private CircleImageView buddyIcon;
        private TextView tvPhotoViewUsername;
        private ImageView imagePhotoViewFavoriteUser;
        private ImageView imageShare;
        private ImageView imageFavorite;
        private TextView tvViewProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageViewSquare = (ImageView) itemView.findViewById(R.id.imageViewSquare);
            imageViewSquare.setOnClickListener(this);
            itemView.setOnClickListener(this);
            buddyIcon = (CircleImageView) itemView.findViewById(R.id.buddyIconPhotoView);
            buddyIcon.setOnClickListener(this);
            tvPhotoViewUsername = (TextView) itemView.findViewById(R.id.photoViewUsername);
            tvPhotoViewUsername.setOnClickListener(this);
            imagePhotoViewFavoriteUser = (ImageView) itemView.findViewById(R.id.photoViewFavoriteUser);
            imageFavorite = (ImageView) itemView.findViewById(R.id.imageFavoritePhotoView);
            imageFavorite.setOnClickListener(this);
            imageShare = (ImageView) itemView.findViewById(R.id.imageSharePhotoView);
            imageShare.setOnClickListener(this);
            tvViewProfile = (TextView) itemView.findViewById(R.id.viewProfile);
            tvViewProfile.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            final Photo photo = mPhotos.get(getLayoutPosition());

            switch (v.getId()){
                case R.id.imageFavoritePhotoView:
                    ImageView favorite = (ImageView) v.findViewById(R.id.imageFavoritePhotoView);
                    if (photo.getIsFavorite()){
                        favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    } else {
                        favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }
                    mListener.recyclerViewFavoriteClicked(mPhotos.get(getLayoutPosition()));
                    break;
                case R.id.imageSharePhotoView:
                    mListener.recyclerViewShareClicked();
                    break;
                case R.id.imageViewSquare:
                    mListener.recyclerViewMainImageClicked(mPhotos.get(getLayoutPosition()), v);
                    break;
                case R.id.photoViewFavoriteUser:
                    mListener.recyclerViewFavoriteUserClicked();
                    break;
                case R.id.viewProfile:
                case R.id.buddyIconPhotoView:
                case R.id.photoViewUsername:
                    mListener.recyclerViewProfileClicked(photo.getOwner());
                    break;
            }
        }
    }
}
