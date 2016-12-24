package com.chzan.dragview.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.chzan.dragview.R;
import com.chzan.dragview.manager.DragHelper;

/**
 * Created by chenzan on 2016/6/17.
 */
public class DragView extends FrameLayout {
    private final String TAG = "DRAGVIEW";

    private ViewDragHelper viewDragHelper;
    private View contentView;
    private float mScrollPercent;//滑动比例
    private boolean finishThisPage = false;
    private Drawable mLeftShadowDrawable;
    private Drawable mTopShadowDrawable;
    private Context mContext;
    private int stateHeight = 0;
    private Activity beforePage;

    public DragView(Context context, ViewGroup decorView) {
        super(context);
        this.mContext = context;
        replaceDecorate(decorView);
        mLeftShadowDrawable = getResources().getDrawable(R.drawable.left_shadow_shape);
        mTopShadowDrawable = getResources().getDrawable(R.drawable.top_shadow_shape);
        initDragView();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                stateHeight = getStateHeight();
            }
        });
    }

    /**
     * 获取状态栏高度
     */
    public int getStateHeight() {
        Rect rect = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    //截取并替换替换decorview的contentView
    private void replaceDecorate(ViewGroup decorView) {
        this.setBackgroundColor(Color.TRANSPARENT);
        View childAt = decorView.getChildAt(0);
        decorView.removeView(childAt);
        contentView = childAt;
        addView(childAt);
        decorView.addView(this, 0);
    }

    private void initDragView() {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallBack());
    }

    public void setBeforePage(Activity beforePage) {
        this.beforePage = beforePage;
    }

    private class DragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //根据设置判断滑动的情况
            if (DragHelper.dragOrientation == DragHelper.HORIZONTAL) {
                if (DragHelper.dragBeginRange == DragHelper.EDGE_LEFT) {
                    boolean edgeTouched = viewDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT, pointerId);
                    if (edgeTouched && child == contentView &&
                            //检查是否可以滑动
                            viewDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL, pointerId)) {
                        return true;
                    } else
                        return false;
                } else {
                    if (child == contentView &&
                            viewDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL, pointerId))
                        return true;
                    else
                        return false;
                }
            } else {
                if (child == contentView && viewDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL, pointerId)) {
                    return true;
                } else
                    return false;
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            Log.e(TAG, left + "");
            if (DragHelper.dragOrientation == DragHelper.HORIZONTAL && left > 0) {
                return left;
            } else
                return 0;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (DragHelper.dragOrientation == DragHelper.VERTICAL && top > 0)
                return top;
            else
                return 0;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
//            Log.e(TAG, "left:" + left + "----" + "top:" + top + "----" + "dx:" + dx + "----" + "dy:" + dy);
            if (DragHelper.dragOrientation == DragHelper.HORIZONTAL) {
                //水平
                mScrollPercent = (left + 0.f) / DragView.this.getMeasuredWidth();
            } else {
                //垂直
                mScrollPercent = (top + 0.f) / DragView.this.getMeasuredHeight();
            }
            if (mScrollPercent >= 0.3) {
                finishThisPage = true;
            } else {
                finishThisPage = false;
            }
            if (beforePage != null) {
                if (DragHelper.dragOrientation == DragHelper.HORIZONTAL)
                    beforePage.getWindow().getDecorView().setTranslationX((DragView.this.getMeasuredWidth() / 4)
                            * (mScrollPercent - 1));
                else
                    beforePage.getWindow().getDecorView().setTranslationY((DragView.this.getMeasuredHeight() / 6)
                            * (mScrollPercent - 1));
            }
            if (slideStateListener != null) {
                slideStateListener.onViewScroll(left, top, dx, dy);
            }
            invalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            switch (state) {
                case ViewDragHelper.STATE_IDLE:
//                    Log.e(TAG, "idle");
                    if (finishThisPage) {
                        if (slideStateListener != null) {
                            slideStateListener.onViewClose();
                        }
                        ((Activity) DragView.this.getContext()).finish();
                        ((Activity) DragView.this.getContext()).overridePendingTransition(0, 0);
                    }
                    if (beforePage != null) {
                        if (DragHelper.dragOrientation == DragHelper.HORIZONTAL)
                            beforePage.getWindow().getDecorView().setTranslationX(0);
                        else
                            beforePage.getWindow().getDecorView().setTranslationY(0);
                    }
                    break;
                case ViewDragHelper.STATE_DRAGGING:
//                    Log.e(TAG, "dragging");
                    if (slideStateListener != null) {
                        slideStateListener.onViewScrollStart();
                    }
                    if (beforePage != null) {
                        if (DragHelper.dragOrientation == DragHelper.HORIZONTAL)
                            beforePage.getWindow().getDecorView().setTranslationX(-DragView.this.getMeasuredWidth() / 4);
                        else
                            beforePage.getWindow().getDecorView().setTranslationY(-DragView.this.getMeasuredHeight() / 6);
                    }
                    break;
                case ViewDragHelper.STATE_SETTLING:
//                    Log.e(TAG, "settling");
                    break;
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
//            Log.e(TAG, "xvel:" + xvel + "----yvel:" + yvel);
            int finalLeft;
            int finalRight;
            if (!finishThisPage) {
                finalLeft = 0;
                finalRight = 0;
                if (slideStateListener != null) {
                    slideStateListener.onViewRelease(xvel, yvel);
                }
            } else {
                if (DragHelper.dragOrientation == DragHelper.HORIZONTAL) {
                    finalLeft = DragView.this.getMeasuredWidth();
                    finalRight = 0;
                } else {
                    finalLeft = 0;
                    finalRight = DragView.this.getMeasuredHeight();
                }
            }
            viewDragHelper.settleCapturedViewAt(finalLeft, finalRight);
            invalidate();
        }
    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean drawContent = child == contentView;
        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (drawContent && viewDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child);
            drawScrim(canvas, child);
        }
        return ret;
    }

    private void drawScrim(Canvas canvas, View child) {
        //绘制
        if (DragHelper.dragOrientation == DragHelper.HORIZONTAL) {
            canvas.clipRect(0, 0, child.getLeft(), getHeight());
        } else {
            canvas.clipRect(0, 0, getRight(), child.getTop() + stateHeight);
        }
        canvas.drawColor(Color.parseColor("#55000000"));
    }

    private Rect mTmpRect = new Rect();

    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mTmpRect;
        //得到当前View的位置
        child.getHitRect(childRect);
        //给drawable设置位置
        if (DragHelper.dragOrientation == DragHelper.HORIZONTAL) {
            mLeftShadowDrawable.setBounds(childRect.left - mLeftShadowDrawable.getIntrinsicWidth(),
                    childRect.top, childRect.left, childRect.bottom);
            mLeftShadowDrawable.draw(canvas);
        } else {
            mTopShadowDrawable.setBounds(childRect.left, childRect.top - mTopShadowDrawable.getIntrinsicHeight(),
                    childRect.right, childRect.top + stateHeight);
            mTopShadowDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    private SlideStateListener slideStateListener;

    public void setSlideStateListener(SlideStateListener slideStateListener) {
        this.slideStateListener = slideStateListener;
    }

    /**
     * 状态监听函数
     */
    public interface SlideStateListener {
        void onViewScrollStart();

        void onViewClose();

        void onViewRelease(float xvel, float yvel);

        void onViewScroll(int left, int top, int dx, int dy);
    }
}
