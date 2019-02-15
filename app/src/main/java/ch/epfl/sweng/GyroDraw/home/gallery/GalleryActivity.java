package ch.epfl.sweng.GyroDraw.home.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbForImages;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.GyroDraw.utils.GlideUtils;
import ch.epfl.sweng.GyroDraw.utils.LayoutUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the gallery, where users can see the pictures they drew.
 */
public class GalleryActivity extends NoBackPressActivity {

    private static final int COLUMNS = 3;

    public static final String POS = "pos";

    private static List<Bitmap> bitmaps;

    public static List<Bitmap> getBitmaps() {
        return new ArrayList<>(bitmaps);
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

        TextView emptyGalleryText = findViewById(R.id.emptyGalleryText);
        emptyGalleryText.setTypeface(typeMuro);

        final RecyclerView recyclerView = findViewById(R.id.galleryList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS));
        recyclerView.setHasFixedSize(true);

        final LocalDbForImages dbHandler = new LocalDbHandlerForImages(this, null, 1);
        bitmaps = dbHandler.getBitmaps(this);

        ImageView deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHandler.removeAll();
                recreate();
            }
        });

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

        // Hide or display the empty gallery text
        LinearLayout galleryLayout = findViewById(R.id.galleryLinearLayout);
        ((ViewManager) galleryLayout).removeView(bitmaps.isEmpty() ? recyclerView : emptyGalleryText);
    }

    /**
     * A {@link RecyclerView.Adapter} used to manage the different pictures in the gallery.
     */
    private class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;
        private final List<Bitmap> images;

        private GalleryAdapter(Context context, List<Bitmap> images) {
            this.context = context;
            this.images = new ArrayList<>(images);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.gallery_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with(context).load(images.get(position))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions()
                            .override(200, 200)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                    .thumbnail(0.5f)
                    .into(((ItemHolder) holder).imageView);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    /**
     * A {@link RecyclerView.ViewHolder} used to hold an {@link ImageView}.
     */
    private static class ItemHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        private ItemHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
        }
    }
}
