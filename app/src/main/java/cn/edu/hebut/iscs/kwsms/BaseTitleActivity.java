package cn.edu.hebut.iscs.kwsms;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by lixueyang on 16-8-16.
 */
public class BaseTitleActivity extends Activity {
    ImageView backArrow;
    TextView textViewTitle;
    TextView textViewRight;
    LinearLayout llActivityLayout;


    private RelativeLayout relativeLayoutTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_title);
        llActivityLayout = (LinearLayout) findViewById(R.id.ll_activity_layout);
        textViewRight=(TextView) findViewById(R.id.textView_right);
        textViewTitle=(TextView) findViewById(R.id.textView_title);
        relativeLayoutTitle = (RelativeLayout) findViewById(R.id.title);
        backArrow=(ImageView) findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     * 隐藏titleBar
     */
    public void hideTitleBar() {
        relativeLayoutTitle.setVisibility(View.GONE);
    }

    /**
     * 设置主界面
     *
     * @param layoutResId
     */
    public void initSetContentView(int layoutResId) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutResId, llActivityLayout, false);
        llActivityLayout.addView(v);
    }

    /**
     * 隐藏左侧返回按钮
     *
     * @param bSetHide
     */
    public void hideTitleImageviewLeft(boolean bSetHide) {
        if (null != backArrow) {
            if (bSetHide) {
                backArrow.setVisibility(View.INVISIBLE);
            } else {
                backArrow.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 隐藏右侧按钮
     *
     * @param bSetHide
     */
    public void hideTitleTextViewRight(boolean bSetHide) {
        if (null != textViewRight) {
            if (bSetHide)
                textViewRight.setVisibility(View.INVISIBLE);
            else
                textViewRight.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 得到右侧按钮
     *
     * @return
     */
    public TextView getTitleTextViewRight() {
        return textViewRight;
    }

    /**
     * 得到左侧按钮
     *
     * @return
     */
    public TextView getTitleTextView() {
        return textViewTitle;
    }

    /**
     * 设置textViewTitle内容
     *
     * @param titleText
     * @return
     */
    public boolean setTitleBarText(String titleText) {

        if (null != textViewTitle) {
            textViewTitle.setText(titleText);
            return true;
        }
        return false;
    }

}
