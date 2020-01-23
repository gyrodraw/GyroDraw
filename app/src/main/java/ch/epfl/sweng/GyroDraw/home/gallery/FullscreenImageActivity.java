package ch.epfl.sweng.GyroDraw.home.gallery;

import static ch.epfl.sweng.GyroDraw.utils.ImageStorageManager.askForStoragePermission;
import static ch.epfl.sweng.GyroDraw.utils.ImageStorageManager.hasExternalWritePermissions;
import static ch.epfl.sweng.GyroDraw.utils.ImageStorageManager.saveImage;

import android.graphics.Bitmap;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.utils.GlideUtils;
import ch.epfl.sweng.GyroDraw.utils.ImageSharer;
import ch.epfl.sweng.GyroDraw.utils.LayoutUtils;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the activity displaying fullscreen an image in the gallery.
 */
public class FullscreenImageActivity extends NoBackPressActivity {

    private boolean sharingMode = false;
    private boolean savingModeRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_fullscreen_image);

        GlideUtils.startBackgroundAnimation(this);

        TextView exitButton = findViewById(R.id.crossText);
        exitButton.setTypeface(typeMuro);
        LayoutUtils.setFadingExitListener(exitButton, this, GalleryActivity.class);

        final int pos = getIntent().getIntExtra(GalleryActivity.POS, 0);

        final List<Bitmap> bitmaps = GalleryActivity.getBitmaps();
        final ImagesPagerAdapter imagesPagerAdapter = new ImagesPagerAdapter(
                getSupportFragmentManager(), bitmaps);

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(imagesPagerAdapter);
        viewPager.setCurrentItem(pos);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
                Fragment fragment = imagesPagerAdapter.getRegisteredFragment(position);
                setSaveButtonListener(((PlaceholderFragment) fragment).bitmap);
                setShareButtonListener(((PlaceholderFragment) fragment).bitmap);
            }

            @Override
            public void onPageSelected(int position) {
                // Not useful
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Not useful
            }
        });
    }

    private void setSaveButtonListener(final Bitmap image) {
        ImageView saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (hasExternalWritePermissions(FullscreenImageActivity.this)) {
                    saveImage(FullscreenImageActivity.this, image);
                } else {
                    savingModeRequest = true;
                    askForStoragePermission(FullscreenImageActivity.this);
                }
            }
        });
    }

    private void setShareButtonListener(final Bitmap image) {
        ImageView shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sharingMode = true;
                ImageSharer.getInstance(FullscreenImageActivity.this).shareImageToFacebook(image);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sharingMode) {
            sharingMode = false;
            return;
        }

        if (savingModeRequest) {
            savingModeRequest = false;
            return;
        }

        ImageSharer sharer = ImageSharer.getInstance();
        if (sharer != null) {
            sharer.setActivity(null);
        }
    }

    /**
     * A placeholder fragment containing the {@link ImageView} for the picture.
     */
    public static class PlaceholderFragment extends Fragment {

        private Bitmap bitmap;

        /**
         * Returns a new instance of this fragment for the given bitmap.
         */
        public static PlaceholderFragment newInstance(Bitmap bitmap) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.bitmap = bitmap;
            return fragment;
        }

        public PlaceholderFragment() {
            // Conventions suggest that it should be provided but not used
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater
                    .inflate(R.layout.gallery_fullscreen_fragment, container, false);

            final ImageView imageView = rootView.findViewById(R.id.detail_image);

            Glide.with(getActivity()).load(bitmap).thumbnail(0.1f).into(imageView);

            return rootView;
        }

    }

    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to one of the
     * images.
     */
    private class ImagesPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Bitmap> bitmaps;
        private SparseArray<Fragment> registeredFragments = new SparseArray<>();

        private ImagesPagerAdapter(FragmentManager fm, List<Bitmap> bitmaps) {
            super(fm);
            this.bitmaps = new ArrayList<>(bitmaps);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public Parcelable saveState() {
            Bundle bundle = (Bundle) super.saveState();
            bundle.putParcelableArray("states", null); // Never maintain any states
            return bundle;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(bitmaps.get(position));
        }

        @Override
        public int getCount() {
            return bitmaps.size();
        }
    }
}
