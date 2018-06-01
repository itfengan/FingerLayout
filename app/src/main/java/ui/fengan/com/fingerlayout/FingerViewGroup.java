package ui.fengan.com.fingerlayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/**
 * @author fengan
 * @email fengan1102@gmail.com
 * @created 2018/6/1
 */
public class FingerViewGroup extends FrameLayout {
    private final static long DURATION = 250;
    private final static int MAX_EXIT_Y = 350;
    private int mTouchSlop;
    private View contentLayout;
    PointF downPoint = new PointF();

    public FingerViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public FingerViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FingerViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentLayout = getChildAt(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downPoint.x = ev.getRawX();
                downPoint.y = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float xDiff = Math.abs(ev.getRawX() - downPoint.x);
                final float yDiff = Math.abs(ev.getRawY() - downPoint.y);
                if (contentLayout != null && yDiff > mTouchSlop && xDiff < mTouchSlop && yDiff > xDiff) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                downPoint.x = 0;
                downPoint.y = 0;
                break;

        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (null != contentLayout) {
                    onActionMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                onActionUp();
                break;
        }
        return true;
    }

    private float mTranslationY;
    private boolean isAnimate = false;
    private onAlphaChangedListener mOnAlphaChangedListener;


    private void onActionMove(MotionEvent event) {
        float moveY = event.getRawY();
        mTranslationY = moveY - downPoint.y;
        float percent = Math.abs(mTranslationY / contentLayout.getHeight());
        float mAlpha = (1 - percent);
        if (mAlpha > 1) {
            mAlpha = 1;
        } else if (mAlpha < 0) {
            mAlpha = 0;
        }
        ViewGroup parent = (ViewGroup) getParent();
        if (null != parent && null != parent.getBackground()) {
            parent.getBackground().mutate().setAlpha((int) (mAlpha * 255));
        }
        //callback
        if (null != mOnAlphaChangedListener) {
            mOnAlphaChangedListener.onTranslationYChanged(mTranslationY);
            mOnAlphaChangedListener.onAlphaChanged(mAlpha);
        }
        setScrollY(-(int) mTranslationY);
    }

    private void onActionUp() {
        if (Math.abs(mTranslationY) > MAX_EXIT_Y) {
            exitWithTranslation();
        } else {
            resetWithTranslation();
        }
    }

    private void exitWithTranslation() {
        ValueAnimator animExit = ValueAnimator.ofFloat(mTranslationY, mTranslationY > 0 ? getHeight() : -getHeight());
        animExit.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isAnimate) {
                    float fraction = (float) animation.getAnimatedValue();
                    setScrollY(-(int) fraction);
                }
            }
        });
        animExit.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimate) {
                    reset();
                    mOnAlphaChangedListener.onFinishAction();
//                    if (getContext() instanceof Activity) {
//                        Activity activity = ((Activity) getContext());
//                        activity.finish();
//                        activity.overridePendingTransition(fadeIn, fadeOut);
//                    }
                    isAnimate = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animExit.setDuration(DURATION);
        animExit.setInterpolator(new LinearInterpolator());
        animExit.start();
    }

    /**
     * 重置位置
     */
    private void resetWithTranslation() {
        ValueAnimator animatorY = ValueAnimator.ofFloat(mTranslationY, 0);
        animatorY.setDuration(DURATION);
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (isAnimate) {
                    mTranslationY = (float) valueAnimator.getAnimatedValue();
                    setScrollY(-(int) mTranslationY);
                }
            }
        });
        animatorY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimate) {
                    mTranslationY = 0;
                    ViewGroup parent = (ViewGroup) getParent();
                    if (null != parent && parent.getBackground() != null) {
                        parent.getBackground().mutate().setAlpha(255);
                    }
                    reset();
                    isAnimate = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorY.setInterpolator(new LinearInterpolator());
        animatorY.start();
    }


    public interface onAlphaChangedListener {
        void onAlphaChanged(float alpha);

        void onTranslationYChanged(float translationY);

        void onFinishAction();
    }

    public void setOnAlphaChangeListener(onAlphaChangedListener alphaChangeListener) {
        mOnAlphaChangedListener = alphaChangeListener;
    }

    private void reset() {
        if (null != mOnAlphaChangedListener) {
            mOnAlphaChangedListener.onTranslationYChanged(mTranslationY);
            mOnAlphaChangedListener.onAlphaChanged(1);
        }
    }
}
