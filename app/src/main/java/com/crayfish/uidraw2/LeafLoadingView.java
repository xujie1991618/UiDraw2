package com.crayfish.uidraw2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * ============================
 * 作    者：crayfish(徐杰)
 * 创建日期：2017/10/13.
 * 描    述：
 * 修改历史：
 * ===========================
 */

public class LeafLoadingView extends View{
    // 淡白色
    private static final int WHITE_COLOR = 0xfffde399;
    // 橙色
    private static final int ORANGE_COLOR = 0xffffa800;
    // 中等振幅大小
    private static final int MIDDLE_AMPLITUDE = 13;
    // 不同类型之间的振幅差距
    private static final int AMPLITUDE_DISPARITY = 5;
    // 总进度
    private static final int TOTAL_PROGRESS = 100;
    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 3000;
    // 叶子旋转一周需要的时间
    private static final long LEAF_ROTATE_TIME = 2000;
    //偏移量
    private static final int OFFSET = 6;
    // 用于控制绘制的进度条距离左／上／下的距离
    private static final int LEFT_MARGIN = 9;
    // 用于控制绘制的进度条距离右的距离
    private static final int RIGHT_MARGIN = 25;
    private int mLeftMargin, mRightMargin;
    //偏移量
    private int mOffset;
    // 叶子飘动一个周期所花的时间
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    // 叶子旋转一周需要的时间
    private long mLeafRotateTime = LEAF_ROTATE_TIME;
    // 中等振幅大小
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // 振幅差
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;

    //当前进度条
    private int mProgress;
    // 所绘制的进度条部分的宽度
    private int mProgressWidth;
    // 当前所在的绘制的进度条的位置
    private int mCurrentProgressPosition;
    // 弧形的半径
    private float mArcRadius;
    private Paint mWhitePaint,mOrangePaint;
    private Paint mBitmapPaint;

    private RectF mWhiteRectF, mOrangeRectF, mArcRectF;

    //叶子
    private Bitmap mLeafBitmap;
    private int mLeafWidth,mLeafHeight;

    //风扇
    private Bitmap mFengShanBitmap;
    private int mFengShanWidth,mFengShanHeight;

    //外框
    private Bitmap mOuterBitmap;
    private int mOuterWidth,mOuterHeight;
    private Rect mOuterSrcRect, mOuterDestRect;
    private int mWidth,mHeight;

    // 用于产生叶子信息
    private LeafFactory mLeafFactory;
    // 产生出的叶子信息
    private List<Leaf> mLeafInfos;
    // 用于控制随机增加的时间不抱团
    private int mAddTime;

    public LeafLoadingView(Context context) {
        super(context,null);
    }

    public LeafLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
        init();
    }

    public LeafLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        mOffset = UiUtils.dipToPx(getContext(),OFFSET);
        mLeftMargin = UiUtils.dipToPx(getContext(),LEFT_MARGIN);
        mRightMargin = UiUtils.dipToPx(getContext(),RIGHT_MARGIN);

        mLeafFloatTime = LEAF_FLOAT_TIME;
        mLeafRotateTime = LEAF_ROTATE_TIME;

        initBitmap();
        initPaint();

        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs();
    }

    private float degrees=0;
    private Matrix matrix = new Matrix();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
        drawLeafs(canvas);
        canvas.drawBitmap(mOuterBitmap,0,0,mBitmapPaint);
        matrix.setTranslate(mOuterWidth - mFengShanWidth - mOffset,mOffset);
        matrix.postRotate(degrees+=5,mOuterWidth - mFengShanWidth/2 - mOffset ,mOffset + mFengShanHeight/2);
        canvas.drawBitmap(mFengShanBitmap,matrix,mBitmapPaint);
        invalidate();
    }

    /**
     * 绘制叶子
     * @param canvas
     */
    private void drawLeafs(Canvas canvas){
        mLeafRotateTime = mLeafRotateTime < 0?LEAF_ROTATE_TIME:mLeafRotateTime;
        long currentTime = System.currentTimeMillis();
        for (int i=0;i<mLeafInfos.size();i++){
            Leaf leaf = mLeafInfos.get(i);
            if(currentTime > leaf.startTime && leaf.startTime!=0){
                //绘制叶子
                getLeafLocation(leaf,currentTime);
                //根据时间计算叶子旋转
                canvas.save();
                Matrix matrix = new Matrix();
                float transX = mLeftMargin + leaf.x;
                float transY = mLeftMargin + leaf.y;
                matrix.postTranslate(transX,transY);
                //通过时间关联旋转角度
                // 通过时间关联旋转角度，则可以直接通过修改LEAF_ROTATE_TIME调节叶子旋转快慢
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime)
                        / (float) mLeafRotateTime;
                int angle = (int) (rotateFraction * 360);
                // 根据叶子旋转方向确定叶子旋转角度
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle
                        + leaf.rotateAngle;
                matrix.postRotate(rotate, transX
                        + mLeafWidth / 2, transY + mLeafHeight / 2);
                canvas.drawBitmap(mLeafBitmap,matrix,mBitmapPaint);
                canvas.restore();
            }else{
                continue;
            }
            if(leaf.x < mCurrentProgressPosition){
                mProgress++;
            }
        }
    }

    /**
     * 获取叶子位置
     * @param leaf
     * @param currentTime
     */
    private void getLeafLocation(Leaf leaf,long currentTime){
        long intervalTime = currentTime - leaf.startTime;
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis()
                    + new Random().nextInt((int) mLeafFloatTime);
        }

        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = (int) (mProgressWidth - mProgressWidth * fraction);
        leaf.y = getLocationY(leaf);
    }

    /**
     * 获取叶子Y
     * @param leaf
     * @return
     */
    private int getLocationY(Leaf leaf){
        // y = A(wx+Q)+h
        float w = (float) ((float)2 * Math.PI/mProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.startType) {
            case LITTLE:
                // 小振幅 ＝ 中等振幅 － 振幅差
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case MIDDLE:
                a = mMiddleAmplitude;
                break;
            case BIG:
                // 小振幅 ＝ 中等振幅 + 振幅差
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
        return (int) ((a * Math.sin(w * leaf.x)) + mArcRadius * 2 / 3);
    }

    /**
     * 绘制进度
     * @param canvas
     */
    private void drawProgress(Canvas canvas){
        if(mProgress >= TOTAL_PROGRESS){
            mProgress = 0;
        }
        mCurrentProgressPosition = mProgressWidth * mProgress /TOTAL_PROGRESS;
        if(mCurrentProgressPosition < mArcRadius){
            //1、绘制ARC
            canvas.drawArc(mArcRectF,90,180,false,mWhitePaint);
            //2、绘制矩形
            mWhiteRectF.left = mArcRadius;
            canvas.drawRect(mWhiteRectF,mWhitePaint);

            //绘制有色ARC
            int angle = (int)Math.toDegrees(Math.cos(mArcRadius - mCurrentProgressPosition)/(float)mArcRadius);
            //起始角度
            int startAngle = 180 - angle;
            //扫过角度
            int sweepAngle = angle * 2;
            canvas.drawArc(mArcRectF,startAngle,sweepAngle,false,mOrangePaint);
        }else{
            mWhiteRectF.left = mCurrentProgressPosition;
            canvas.drawRect(mWhiteRectF,mWhitePaint);

            canvas.drawArc(mArcRectF,90,180,false,mOrangePaint);
            mOrangeRectF.right = mCurrentProgressPosition;
            canvas.drawRect(mOrangeRectF,mOrangePaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mProgressWidth = mOuterWidth - mLeftMargin - mRightMargin;
        mArcRadius = mFengShanWidth/2;
        mWhiteRectF = new RectF(mLeftMargin/2+mArcRadius,mLeftMargin,mOuterWidth-mFengShanHeight,mOuterHeight-mLeftMargin);
        mOrangeRectF = new RectF(mLeftMargin/2+mArcRadius,mLeftMargin,mCurrentProgressPosition,mOuterHeight-mLeftMargin);
        mArcRectF = new RectF(mLeftMargin,mLeftMargin,mArcRadius*2,mOuterHeight-mLeftMargin);
    }

    //初始化图片
    private void initBitmap(){
        mLeafBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.leaf);
        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();

        mFengShanBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.fengshan);
        mFengShanWidth = mFengShanBitmap.getWidth();
        mFengShanHeight = mFengShanBitmap.getHeight();

        mOuterBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.leaf_kuang);
        mOuterWidth = mOuterBitmap.getWidth();
        mOuterHeight = mOuterBitmap.getHeight();

    }

    //初始化画笔
    private void initPaint(){
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);
    }

    private enum StartType{
        LITTLE,MIDDLE,BIG
    }

    //叶子
    private class Leaf{
        //位置
        float x,y;
        //叶子飘动振幅
        StartType startType;
        //旋转角度
        int rotateAngle;
        //旋转方向 0顺 1逆
        int rotateDirection;
        //开始时间
        long startTime;
    }

    //叶子工厂
    public class LeafFactory{
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        //生成一个叶子信息
        public Leaf generateLeaf(){
            Leaf leaf = new Leaf();
            int randomType = random.nextInt(3);
            StartType type = StartType.MIDDLE;
            switch (randomType){
                case 0:
                    break;
                case 1:
                    type = StartType.LITTLE;
                    break;
                case 2:
                    type = StartType.BIG;
                    break;
                default:
                    break;
            }
            leaf.startType = type;
            //随机起始旋转的角度
            leaf.rotateAngle = random.nextInt(360);
            //随机旋转方向
            leaf.rotateDirection = random.nextInt(2);
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime * 2));
            leaf.startTime = System.currentTimeMillis() + mAddTime;
            return leaf;
        }

        public List<Leaf> generateLeafs(){
            return generateLeafs(MAX_LEAFS);
        }

        // 根据传入的叶子数量产生叶子信息
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }
    }
}
