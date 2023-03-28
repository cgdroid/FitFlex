package com.tmhnry.fitflex.view;

import android.app.Activity;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageButton;

public class ViewUnit {
    public static class ImageButton extends AppCompatImageButton {
        private  MotionEventListener listener;

        public ImageButton(Activity context){
            super(context);
            listener = null;
        }

        public ImageButton(Activity context, AttributeSet attrs){
            super(context, attrs);
            listener = null;
        }

        public ImageButton(Activity context, MotionEventListener listener) {
            super(context);
            this.listener = listener;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    listener.onActionDown();
                    return true;
                case MotionEvent.ACTION_UP:
                    return true;
            }

            return false;
        }

        // performClick is called when MotionEvent.ACTION_UP is triggered, so putting listener.onActionUp()
        // inside switch case ACTION_UP is redundant
        @Override
        public boolean performClick() {
            super.performClick();
            listener.onActionUp();
            return true;
        }

        public interface MotionEventListener {
            void onActionDown();
            void onActionUp();
        }
    }
}
