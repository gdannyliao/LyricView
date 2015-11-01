package com.liao.danny.lyricview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;

/**
 * Created by jkl on 15/10/31.
 */
public class LyricView extends View {
    private ArrayList<Lyric> mLyrics;
    private int mLineHeight = 50;
    private float mNormalTextSize = 40f;
    private float mHeightLightTextSize = 48f;
    private int mMaxLine = 1;
    private int mHeightLightLine = 0;
    private int mTopLine = 0;
    private int mPaddingTop = 20;
    private Paint mNormalPaint;
    private Paint mHeightLightPaint;
    private Scroller mScroller;

    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mNormalPaint = new Paint();
        mNormalPaint.setColor(Color.BLACK);
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setTextSize(mNormalTextSize);

        mHeightLightPaint = new Paint();
        mHeightLightPaint.setColor(Color.RED);
        mHeightLightPaint.setAntiAlias(true);
        mHeightLightPaint.setTextSize(mHeightLightTextSize);

        mScroller = new Scroller(getContext());
    }
    public void setLyrics(ArrayList<Lyric> list) {
        if (list == null) {
            throw new IllegalArgumentException("lyric list is null");
        }
        mLyrics = list;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        mMaxLine = measuredHeight / mLineHeight;
        mHeightLightLine = mMaxLine / 2;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        Scroller scroller = mScroller;
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    private float computeLineOffset(float y) {
        float v = y - starty;
        int offset = (int) (Math.abs(v) / mLineHeight);
        Log.d("LyricView", "computeLineOffset() called with: " + "v = [" + v + "]");
        //当偏移量达到mLineHeight时，将偏移量缩小mLineHeight个长度，同时mTopLine相应的增加或减少一行
        if (v > 0) {
            mTopLine -= offset;
            v -= offset * mLineHeight;
        } else {
            mTopLine += offset;
            v += offset * mLineHeight;
        }
        Log.d("LyricView", "computeLineOffset() returned: " + v);
        return v;
    }
    private float starty;
    private float lasty;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        float deltay = lasty - y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                starty = y;
                lasty = starty;
                return true;
            case MotionEvent.ACTION_MOVE:
                scrollTo(0, (int) computeLineOffset(y));
                break;
            case MotionEvent.ACTION_UP:
                starty = 0;
                break;
        }
        lasty = y;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ArrayList<Lyric> lyrics = mLyrics;
        if (lyrics == null) {
            return;
        }
        int topLine = mTopLine;
        int paddingTop = mPaddingTop;
        int lineHeight = mLineHeight;
        Paint normalPaint = mNormalPaint;
        Paint heightLightPaint = mHeightLightPaint;

        int maxLine = mMaxLine;
        int heightLightLine = mHeightLightLine;

        for (int i = 0; i < maxLine; i++) {
            int j = topLine + i;
            if (j >= lyrics.size() || j < 0) continue;
            Lyric lyric = lyrics.get(j);
            if (j == heightLightLine) {
                //FIXME 滚出边界时间隔太大的问题
                canvas.drawText(lyric.getText(), 0, paddingTop + i * lineHeight, heightLightPaint);
            } else {
                canvas.drawText(lyric.getText(), 0, paddingTop + i* lineHeight, normalPaint);
            }
        }
    }

    public void start() {

    }
    public void stop() {

    }
}
