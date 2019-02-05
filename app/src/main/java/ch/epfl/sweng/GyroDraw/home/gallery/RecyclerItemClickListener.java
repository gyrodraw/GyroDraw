package ch.epfl.sweng.GyroDraw.home.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && listener != null && gestureDetector.onTouchEvent(e)) {
            listener.onItemClick(childView, view.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
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
