package cn.edu.hebut.iscs.kwsms.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.edu.hebut.iscs.kwsms.R;

public class MyBasicListDialog extends Dialog {
	private Context context;
	private OnCustomDialogListener customDialogListener;
	private List<String> oprations;
	private ListView listViewOpration;
	private TextView textViewTitle;
	private ArrayAdapter<String> adapter;
	private String title;

	public MyBasicListDialog(Context context) {
		super(context);
		this.context = context;
	}

	public MyBasicListDialog(Context context, int theme,
			OnCustomDialogListener customDialogListener,
			List<String> oprations, String title) {
		super(context, theme);
		this.customDialogListener = customDialogListener;
		this.oprations = oprations;
		this.context = context;
		this.title = title;
	}

	public interface OnCustomDialogListener {
		public void back(int type);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_my_all_list_dialog);
		textViewTitle = (TextView) findViewById(R.id.textView_opration_name);
		textViewTitle.setText(title);
		listViewOpration = (ListView) findViewById(R.id.listView_opration);
		adapter = new ArrayAdapter<String>(context,
				R.layout.item_for_opration_list, oprations);
		listViewOpration.setAdapter(adapter);
		listViewOpration.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == oprations.size() - 1) {
					MyBasicListDialog.this.dismiss();
				} else {
					customDialogListener.back(position);
					MyBasicListDialog.this.dismiss();
				}
			}
		});
	}
}
