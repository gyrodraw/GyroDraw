package ch.epfl.sweng.GyroDraw.home.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbForImages;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.GyroDraw.utils.LayoutUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

/**
 * TODO
 */
public class GalleryActivity extends NoBackPressActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ((TextView) findViewById(R.id.galleryText)).setTypeface(typeMuro);

        TextView exitButton = findViewById(R.id.crossText);
        LayoutUtils.setFadingExitListener(exitButton, this);

        RecyclerView mRecyclerView = findViewById(R.id.galleryList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);

        LocalDbForImages dbHandler = new LocalDbHandlerForImages(this, null, 1);
        GalleryAdapter mAdapter = new GalleryAdapter(this, dbHandler.getBitmapsFromDb(this));
        mRecyclerView.setAdapter(mAdapter);
    }


    private class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<Bitmap> data;

        private GalleryAdapter(Context context, List<Bitmap> data) {
            this.context = context;
            this.data = data;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.gallery_item, parent, false);
            viewHolder = new ItemHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with(context).load(data.get(position))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions()
                            .override(200, 200)
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .thumbnail(0.5f)
                    .into(((ItemHolder) holder).mImg);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private static class ItemHolder extends RecyclerView.ViewHolder {

        private final ImageView mImg;

        private ItemHolder(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.itemImage);
        }
    }
}
