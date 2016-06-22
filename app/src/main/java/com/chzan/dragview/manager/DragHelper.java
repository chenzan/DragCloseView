package com.chzan.dragview.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IntDef;
import android.view.ViewGroup;
import android.view.Window;

import com.chzan.dragview.view.DragView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * builder
 * 需要设置相应的activity的theme中style属性windowIsTranslucent
 * Created by chenzan on 2016/6/21.
 */
public class DragHelper {
    private ViewGroup mDecorView;

    public static int dragBeginRange;
    public static int dragOrientation;

    @IntDef({WHOLE, EDGE_LEFT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DragBeginRangeMode {
    }

    public static final int WHOLE = 0;
    public static final int EDGE_LEFT = 1;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private DragHelper(Builder builder) {
        this.mDecorView = builder.mDecorView;
        this.dragBeginRange = builder.dragBeginRange;
        this.dragOrientation = builder.dragOrientation;
        this.mDecorView.setBackgroundColor(Color.TRANSPARENT);
        this.mDecorView.setBackgroundDrawable(null);
        ((Activity) mDecorView.getContext()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DragView dragView = new DragView(mDecorView.getContext(), this.mDecorView);
        dragView.setSlideStateListener(builder.slideStateListener);
        dragView.setBeforePage(builder.beforeActivity);
    }

    public static class Builder {
        private ViewGroup mDecorView;
        private int dragBeginRange;
        private int dragOrientation;
        private DragView.SlideStateListener slideStateListener;
        private Activity beforeActivity;

        public Builder() {
            //new set params
        }

        public Builder setCurrentPage(Context context) {
            if (context instanceof Activity) {
                Window window = ((Activity) context).getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDecorView = (ViewGroup) window.getDecorView();
            } else {
                throw new RuntimeException("this context is not activity");
            }
            return this;
        }

        /**
         * 设置响应的范围
         *
         * @param dragBeginRange
         * @return
         */
        public Builder setDragBeginRange(@DragBeginRangeMode int dragBeginRange) {
            this.dragBeginRange = dragBeginRange;
            return this;
        }

        /**
         * 设置移动的方向
         *
         * @param orientation
         * @return
         */
        public Builder setDragOrientation(@OrientationMode int orientation) {
            this.dragOrientation = orientation;
            return this;
        }

        /**
         * 一些状态的监听
         *
         * @param slideStateListener
         * @return
         */
        public Builder setSlideStateListener(DragView.SlideStateListener slideStateListener) {
            this.slideStateListener = slideStateListener;
            return this;
        }

        public DragHelper build() {
            if (mDecorView == null) {
                throw new RuntimeException("decorView must set");
            }
            return new DragHelper(this);
        }

        public Builder setBeforeLastPage(Activity activity) {
            this.beforeActivity = activity;
            return this;
        }
    }

    public static class SimpleSlideStateListener implements DragView.SlideStateListener {

        @Override
        public void onViewScrollStart() {

        }

        @Override
        public void onViewClose() {

        }

        @Override
        public void onViewRelease(float xvel, float yvel) {

        }

        @Override
        public void onViewScroll(int left, int top, int dx, int dy) {

        }
    }
}
