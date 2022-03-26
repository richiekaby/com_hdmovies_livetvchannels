package com.hdmovies.livetvchannels.helper;

import android.content.Context;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


public class BottomNavigationBehavior extends CoordinatorLayout.Behavior<BubbleNavigationConstraintView> {

    public BottomNavigationBehavior() {
        super();
    }

    public BottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, BubbleNavigationConstraintView child, View dependency) {
        boolean dependsOn = dependency instanceof FrameLayout;
        return dependsOn;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, BubbleNavigationConstraintView child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, BubbleNavigationConstraintView child, View target, int dx, int dy, int[] consumed) {
        if (dy < 0) {
            showBottomNavigationView(child);
        } else if (dy > 0) {
            hideBottomNavigationView(child);
        }
    }

    private void hideBottomNavigationView(BubbleNavigationConstraintView view) {
        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(BubbleNavigationConstraintView view) {
        view.animate().translationY(0);
    }
}
