package ch.epfl.sweng.GyroDraw.home.gallery;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Class representing an {@link OnItemClickListener} for the recycler view used in the gallery.
 */
class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private final OnItemClickListener listener;

    private final GestureDetector gestureDetector;

    RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        this.listener = listener;
        this.gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent motionEvent) {
                        return true;
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        View childView = view.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (childView != null && listener != null && gestureDetector.onTouchEvent(motionEvent)) {
            listener.onItemClick(childView, view.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        // Not useful here
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // Not useful here
    }

    /**
     * Interface representing a listener for an item click.
     */
    public interface OnItemClickListener {

        /**
         * Processes a click on an item at the given position and contained in the given view.
         *
         * @param view the item's view container
         * @param position the item's position
         */
        void onItemClick(View view, int position);
    }
}
