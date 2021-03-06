package com.stratagile.pnrouter.entity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stratagile.pnrouter.R;


/**
 * *********************************************************
 * <pre>
 * PROJECT: QQNavigationDemo
 * INTRODUCATION: //todo
 * DESCRIPTION: //todo
 * DATE: 2017/04/4:24 PM
 * AUTHOR: shibin1990
 * Email: shib90@qq.com
 * </pre>
 * *********************************************************
 */

public class MenuItemView extends LinearLayout {

    private TextView mTvText;
    private TextView mTvCount;
    private ImageView mIvIcon;

    public MenuItemView(Context context) {
        this(context, null);
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MenuItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.layout_menu_item, this);
        mTvText = (TextView) findViewById(R.id.tv_text);
        mTvCount = (TextView) findViewById(R.id.tv_count);
        mIvIcon = (ImageView) findViewById(R.id.iv_icon);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView);

        String text = typedArray.getString(R.styleable.MenuItemView_itemText);
        String count = typedArray.getString(R.styleable.MenuItemView_itemCount);
        int src = typedArray.getResourceId(R.styleable.MenuItemView_itemSrc, -1);

        if (text != null && !TextUtils.isEmpty(text)) {
            mTvText.setText(text);

        }
        if (count != null && !TextUtils.isEmpty(count)) {
            mTvCount.setText(count);
            int countTemp = Integer.valueOf(count);
            if(countTemp > 0)
            {
                mTvCount.setVisibility(VISIBLE);
            }else{
                mTvCount.setVisibility(GONE);
            }
        }
        if (src != -1) {
            mIvIcon.setImageResource(src);
        }
        typedArray.recycle();
    }

    public void setImageResource(int resId) {
        mIvIcon.setImageResource(resId);
    }
    public void setBackGroundResource(Drawable background) {
        this.setBackground(background);//context.getResources().getDrawable(R.drawable.input_background_press)
    }
    public void setText(@StringRes int resId) {
        mTvText.setText(resId);
    }

    public void setText(String text) {
        mTvText.setText(text);
    }

    public void getText() {
        mTvText.getText();
    }

    public void setCount(int count) {
        mTvCount.setText(count+"");
        if(count > 0)
        {
            mTvCount.setVisibility(VISIBLE);
        }else{
            mTvCount.setVisibility(GONE);
        }

    }
}
