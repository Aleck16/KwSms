package cn.edu.hebut.iscs.kwsms.view;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.hebut.iscs.kwsms.R;

public class MyProgressDialog extends Dialog {
    private static MyProgressDialog myProgressDialog = null;
    private static Context mContext = null;

    public MyProgressDialog(Context context) {
        super(context);
        mContext = context;
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;

    }

    public static MyProgressDialog createDialog(Context context) {
        myProgressDialog = new MyProgressDialog(context,
                R.style.MyProgressDialog);
        myProgressDialog.setContentView(R.layout.layout_my_progress_dialog);
        myProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return myProgressDialog;

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (myProgressDialog == null) {
            return;
        }
        ImageView imageView = (ImageView) myProgressDialog
                .findViewById(R.id.loadingImageView);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                mContext, R.anim.progress_round);
        imageView.startAnimation(hyperspaceJumpAnimation);
    }

    public MyProgressDialog setMessage(String strMessage) {
        TextView tvMsg = (TextView) myProgressDialog
                .findViewById(R.id.id_tv_loadingmsg);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
        return myProgressDialog;
    }

    public static void startProgressDialog(Context context, String msg) {
        if (myProgressDialog == null) {
            myProgressDialog = MyProgressDialog.createDialog(context);
            myProgressDialog.setMessage(msg);
        }
        myProgressDialog.show();
    }

    public static void stopProgressDialog() {
        if (myProgressDialog != null) {
            myProgressDialog.dismiss();
            myProgressDialog = null;
        }
    }

}
