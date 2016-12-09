package com.ubergeek42.WeechatAndroid.utils;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.ubergeek42.WeechatAndroid.R;
import com.ubergeek42.WeechatAndroid.service.P;

public class ToolbarController implements ViewTreeObserver.OnGlobalLayoutListener {
    final Toolbar toolbar;
    final ActionBar actionBar;
    final View root;

    boolean shown = true;
    boolean keyboardVisible = false;

    public ToolbarController(AppCompatActivity activity) {
        this.toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        this.actionBar = activity.getSupportActionBar();
        this.root = activity.findViewById(android.R.id.content);
        root.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void onUserScroll(int bottomHidden, int prevBottomHidden) {
        if (!canAutoHide()) return;
        if (bottomHidden > prevBottomHidden) hide();
        else if (bottomHidden < prevBottomHidden) show();
    }

    public void onPageChangedOrSelected() {
        show();
    }

    public void onSoftwareKeyboardStateChanged(boolean visible) {
        if (!canAutoHide()) return;
        if (keyboardVisible == visible) return;
        keyboardVisible = visible;
        if (visible) hide();
        else show();
    }

    private boolean canAutoHide() {
        if (P.autoHideActionbar)
            return true;
        show();
        return false;
    }

    private void show() {
        if (shown) return;
        shown = true;
        if (android.os.Build.VERSION.SDK_INT >= 14)
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        else
            actionBar.show();
    }

    private void hide() {
        if (!shown) return;
        shown = false;
        if (android.os.Build.VERSION.SDK_INT >= 14)
            toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
        else
            actionBar.hide();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void onGlobalLayout() {
        if (!canAutoHide()) return;
        // if more than 300 pixels, its probably a keyboard...
        int heightDiff = root.getRootView().getHeight() - root.getHeight();
        if (heightDiff > 300)
            onSoftwareKeyboardStateChanged(true);
        else if (heightDiff < 300)
            onSoftwareKeyboardStateChanged(false);
    }
}