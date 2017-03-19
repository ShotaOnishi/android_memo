package com.example.apple.mymemoapp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Created by apple on 2017/03/19.
 */

public class MemoEditText extends AppCompatEditText{

    //直線
    private static final int SOLID = 1;
    //破線
    private static final int DASH = 2;
    //通常の太さ
    private static final int NORMAL = 4;
    //太線
    private static final int BOLD = 8;

    //このViewの横幅
    private int mMeasuredWidth;
    //１行の太さ
    private int mLineHeight;
    //画面上に表示可能な行数
    private int mDisplayLineCount;

    //罫線のパス
    private Path mPath;
    //「どのように描画するか」を保持する
    private Paint mPaint;

    public MemoEditText(Context context){
        this(context, null);
    }

    public MemoEditText(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    public MemoEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mPath = new Path();
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.STROKE);

        if(attrs != null && !isInEditMode()){
            //属性情報を取得
            int lineEffectBit;
            int lineColor;

            Resources resources = context.getResources();
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MemoEditText);

            try{
                //属性に指定された値を取得
                lineEffectBit = typedArray.getInteger(R.styleable.MemoEditText_lineEffect, SOLID);
                lineColor = typedArray.getColor(R.styleable.MemoEditText_lineColor, Color.GRAY);
            }finally{
                typedArray.recycle();
            }

            //罫線のエフェクタを指定
            if((lineEffectBit &  DASH) == DASH){
                //破線が指定されている場合
                DashPathEffect effect = new DashPathEffect(new float[]{
                        resources.getDimension(R.dimen.text_rule_interval_on),
                        resources.getDimension(R.dimen.text_rule_interval_on),
                        resources.getDimension(R.dimen.text_rule_interval_off)},
                0f);
                mPaint.setPathEffect(effect);
            }

            float strokeWidth;
            if((lineEffectBit & BOLD) == BOLD){
                //太線が指定されている場合
                strokeWidth = resources.getDimension(R.dimen.text_rule_width_bold);
            }else{
                strokeWidth = resources.getDimension(R.dimen.text_rule_width_normal);
            }
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setColor(lineColor);
        }
    }

    //横幅と高さを設定するメソッド
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //横幅
        mMeasuredWidth = getMeasuredWidth();
        //高さ
        int measuredHeight = getMeasuredHeight();
        //１行の高さ
        mLineHeight = getLineHeight();

        //画面内に何行表示できるか
        mDisplayLineCount = measuredHeight / mLineHeight;
    }

    //描画処理のメソッド
    @Override
    protected void onDraw(Canvas canvas){
        //パディング
        int paddingTop = getExtendedPaddingTop();
        //Y軸方向にスクロールされている量
        int scrollY = getScrollY();
        //画面に表示されている最初の行
        int firstVisibleLine = getLayout().getLineForVertical(scrollY);
        //画面に表示されている最後の行
        int lastVisibleLine = firstVisibleLine + mDisplayLineCount;

        mPath.reset();
        for(int i = firstVisibleLine; i <= lastVisibleLine; i++){
            //行の左端に移動
            mPath.moveTo(0, i * mLineHeight + paddingTop);
            //右端に線を引く
            mPath.lineTo(mMeasuredWidth, i * mLineHeight + paddingTop);
        }

        //Pathの描写
        canvas.drawPath(mPath, mPaint);

        super.onDraw(canvas);
    }


}
