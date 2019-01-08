package ch.epfl.sweng.GyroDraw.home.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbForImages;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.GyroDraw.utils.GlideUtils;
import ch.epfl.sweng.GyroDraw.utils.LayoutUtils;

/**
 * Class representing the gallery, where users can see the pictures they drew.
 */
public class GalleryActivity extends NoBackPressActivity {

    private static final int COLUMNS = 3;
    public static final String POS = "pos";

    private static List<Bitmap> bitmaps;

    public static List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_gallery);

        GlideUtils.startBackgroundAnimation(this);

        ((TextView) findViewById(R.id.galleryText)).setTypeface(typeMuro);

        TextView exitButton = findViewById(R.id.crossText);
        exitButton.setTypeface(typeMuro);
        LayoutUtils.setFadingExitHomeListener(exitButton, this);

        RecyclerView recyclerView = findViewById(R.id.galleryList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS));
        recyclerView.setHasFixedSize(true);

        LocalDbForImages dbHandler = new LocalDbHandlerForImages(this, null, 1);
        bitmaps = dbHandler.getBitmapsFromDb(this);

        GalleryAdapter adapter = new GalleryAdapter(this, bitmaps);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(),
                                FullscreenImageActivity.class);
                        intent.putExtra(POS, position);
                        startActivity(intent);
                    }
                }));
    }


    private class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;
        private final List<Bitmap> data;

        private GalleryAdapter(Context context, List<Bitmap> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.gallery_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with(context).load(data.get(position))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions()
                            .override(200, 200)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
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
