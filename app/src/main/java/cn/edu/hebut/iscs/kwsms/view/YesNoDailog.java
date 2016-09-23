package cn.edu.hebut.iscs.kwsms.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.edu.hebut.iscs.kwsms.R;

public class YesNoDailog extends Dialog {
	private OnCustomDialogListener customDialogListener;
	private String content, title;
	private final static int CANCLE = 0;
	private final static int YES = 1;
	private final static int NO = 2;

	public YesNoDailog(Context context) {
		super(context);
	}

	public YesNoDailog(Context context, int theme,
			OnCustomDialogListener customDialogListener, String title,
			String content) {
		super(context, theme);
		this.customDialogListener = customDialogListener;
		this.title = title;
		this.content = content;
	}

	public interface OnCustomDialogListener {
		public void back(int type);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_yesorno_dailog);
		TextView buttonYes = (TextView) findViewById(R.id.button_yes);
		TextView buttonNo = (TextView) findViewById(R.id.button_no);
		TextView buttonCancle = (TextView) findViewById(R.id.button_cancle);
		TextView textViewContent = (TextView) findViewById(R.id.textView_content);
		TextView textViewTitle = (TextView) findViewById(R.id.textView_title);
		buttonYes.setOnClickListener(clickListener);
		buttonNo.setOnClickListener(clickListener);
		buttonCancle.setOnClickListener(clickListener);
		textViewTitle.setText(title);
		textViewContent.setText(content);
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_yes:
				customDialogListener.back(YES);
				break;
			case R.id.button_no:
				customDialogListener.back(NO);
				break;
			case R.id.button_cancle:
				customDialogListener.back(CANCLE);
				break;
			default:
				break;
			}
			YesNoDailog.this.dismiss();
		}
	};
}
