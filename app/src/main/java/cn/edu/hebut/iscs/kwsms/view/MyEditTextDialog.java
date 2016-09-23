package cn.edu.hebut.iscs.kwsms.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.edu.hebut.iscs.kwsms.R;

public class MyEditTextDialog extends Dialog {
	private OnCustomDialogListener customDialogListener;
	private String content;
	private String phone;
	private final static int CANCLE = 0;
	private final static int YES = 1;
	private final static int NO = 2;
	private EditText editTextSmsContent;

	public MyEditTextDialog(Context context) {
		super(context);
	}

	public MyEditTextDialog(Context context, int theme,
			OnCustomDialogListener customDialogListener, String content,String phone) {
		super(context, theme);
		this.customDialogListener = customDialogListener;
		this.content = content;
		this.phone=phone;
	}

	public interface OnCustomDialogListener {
		public void back(int type, EditText editTextSmsContent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_my_edittext_dialog);
		Button buttonYes = (Button) findViewById(R.id.button_yes);
		Button buttonNo = (Button) findViewById(R.id.button_no);
		Button buttonCancle = (Button) findViewById(R.id.button_cancle);
		TextView textViewContent = (TextView) findViewById(R.id.textView_content);
		TextView textViewPhone = (TextView) findViewById(R.id.textView_phone);
		editTextSmsContent = (EditText) findViewById(R.id.editText_expert_name);
		buttonYes.setOnClickListener(clickListener);
		buttonNo.setOnClickListener(clickListener);
		buttonCancle.setOnClickListener(clickListener);
		textViewContent.setText(content);
		textViewPhone.setText("电话号码为："+phone);
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_yes:
					customDialogListener.back(YES, editTextSmsContent);
				break;
			case R.id.button_no:
					customDialogListener.back(NO, editTextSmsContent);
				break;
			case R.id.button_cancle:
				customDialogListener.back(CANCLE, editTextSmsContent);
				break;
			default:
				break;
			}
			MyEditTextDialog.this.dismiss();
		}
	};

}
