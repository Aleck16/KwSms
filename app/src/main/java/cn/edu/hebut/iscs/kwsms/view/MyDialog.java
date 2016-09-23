package cn.edu.hebut.iscs.kwsms.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.edu.hebut.iscs.kwsms.R;

public class MyDialog extends Dialog {
	private OnCustomDialogListener customDialogListener;
	private String content, yes, no, title;
	private final static int YES = 1;
	private final static int NO = 0;

	public MyDialog(Context context) {
		super(context);
	}

	public MyDialog(Context context, int theme,
			OnCustomDialogListener customDialogListener, String title,
			String content, String yes, String no) {
		super(context, theme);
		this.customDialogListener = customDialogListener;
		this.title = title;
		this.content = content;
		this.yes = yes;
		this.no = no;
	}

	public interface OnCustomDialogListener {
		public void back(int type);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_my_dialog);
		TextView buttonYes = (TextView) findViewById(R.id.button_yes);
		TextView buttonNo = (TextView) findViewById(R.id.button_no);
		TextView textViewContent = (TextView) findViewById(R.id.textView_content);
		TextView textViewTitle = (TextView) findViewById(R.id.textView_title);
		buttonYes.setOnClickListener(clickListener);
		buttonNo.setOnClickListener(clickListener);
		textViewTitle.setText(title);
		textViewContent.setText(content);
		buttonYes.setText(yes);
		buttonNo.setText(no);

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

			default:
				break;
			}
			MyDialog.this.dismiss();
		}
	};

}
