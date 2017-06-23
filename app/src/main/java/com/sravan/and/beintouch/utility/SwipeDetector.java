package com.sravan.and.beintouch.utility;

import android.view.MotionEvent;
import android.view.View;

import timber.log.Timber;

/**
 * Created by HP on 6/23/2017.
 */

public class SwipeDetector implements View.OnTouchListener {

    public enum Action {
        LR, // Left to Right
        RL, // Right to Left
        None // when no action was detected
    }

    private static final String logTag = "SwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downX, upX;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                mSwipeDetected = Action.None;
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();

                float deltaX = downX - upX;
                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        Timber.d("Swipe Left to Right");
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        Timber.d("Swipe Right to Left");
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                }
                return true;
            }
        }
        return false;
    }
}