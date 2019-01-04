package ch.epfl.sweng.GyroDraw.home.gallery;

import android.graphics.Bitmap;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ch.epfl.sweng.GyroDraw.BaseActivity;
import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.utils.GlideUtils;
import ch.epfl.sweng.GyroDraw.utils.ImageStorageManager;
import ch.epfl.sweng.GyroDraw.utils.LayoutUtils;

/**
 * Class representing the activity displaying fullscreen an image in the gallery.
 */
public class FullscreenImageActivity extends NoBackPressActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_fullscreen_image);

        GlideUtils.startBackgroundAnimation(this);

        TextView exitButton = findViewById(R.id.crossText);
        exitButton.setTypeface(typeMuro);
        LayoutUtils.setFadingExitListener(exitButton, this, GalleryActivity.class);

        final int pos = getIntent().getIntExtra("pos", 0);

        final List<Bitmap> bitmaps = GalleryActivity.getBitmaps();
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(), bitmaps);

        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);

        ImageView saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (ImageStorageManager.hasExternalWritePermissions(FullscreenImageActivity.this)) {
                    ImageStorageManager.saveImage(FullscreenImageActivity.this, bitmaps.get(pos));
                } else {
                    ImageStorageManager.askForStoragePermission(FullscreenImageActivity.this);
                }
            }
        });
    }

    /**
     * A placeholder fragment containing the {@link ImageView} for the picture.
     */
    public static class PlaceholderFragment extends Fragment {

        private Bitmap bitmap;
        private static final String ARG_BITMAP = "bitmap";

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            this.bitmap = args.getParcelable(ARG_BITMAP);
        }

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(Bitmap bitmap) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putParcelable(ARG_BITMAP, bitmap);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
     * sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Bitmap> data;

        private SectionsPagerAdapter(FragmentManager fm, List<Bitmap> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(data.get(position));
        }

        @Override
        public int getCount() {
            return data.size();
        }
    }
}
