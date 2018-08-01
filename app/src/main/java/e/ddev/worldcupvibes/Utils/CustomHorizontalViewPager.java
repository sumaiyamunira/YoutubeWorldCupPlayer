package e.ddev.worldcupvibes.Utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Sumaiya Munira on 6/24/2018.
 */

public class CustomHorizontalViewPager extends ViewPager {

    private static final String TAG = "VerticalViewPager";
    private boolean isDisable;

    public void disableViewpager(boolean isDisable) {
        this.isDisable = isDisable;
    }

    public CustomHorizontalViewPager(Context context) {
        this(context, null);
    }

    public CustomHorizontalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = super.onInterceptTouchEvent(event);
        if (isDisable) {
            return false;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDisable) {
            return false;
        }
        return super.onTouchEvent(event);
    }


}