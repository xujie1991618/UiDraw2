package com.crayfish.uidraw2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * ============================
 * 作    者：crayfish(徐杰)
 * 创建日期：2017/9/27.
 * 描    述：
 * 修改历史：
 * ===========================
 */

public class OOView extends ViewGroup{

    private static final String TAG = "OOView";

    public OOView(Context context) {
        super(context);
    }

    public OOView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        for (int i = 0;i<getChildCount();i++){
//            View view = getChildAt(i);
//            Log.d(TAG,String.valueOf(getRight()));
//            measureChild(view,widthMeasureSpec,heightMeasureSpec);
//        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0;i<getChildCount();i++){
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            int left = ( r - width)/2;
            int top = (b - height)/2;
            view.layout(left,top,left+width,top+height);
        }
    }
}
