package cn.edu.hebut.iscs.kwsms.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.hebut.iscs.kwsms.R;
import cn.edu.hebut.iscs.kwsms.entity.ReplyStateInfo;
import cn.edu.hebut.iscs.kwsms.util.DateTimeUtil;

public class ReplyAdapter extends ArrayAdapter<ReplyStateInfo> {
    private Context mContext;
    private LayoutInflater mInflater;

    public ReplyAdapter(Context context) {
        super(context, R.layout.item_for_reply_sms);
        this.mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_for_reply_sms,
                    parent, false);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }


        final ReplyStateInfo replyStateInfo = getItem(position);
        String time = DateTimeUtil.longTimeToStrDate(
                Long.valueOf(replyStateInfo.getReplyTime()),
                DateTimeUtil.format_1);
        switch (replyStateInfo.getIsTelValid()) {
            case "1":
                switch (replyStateInfo.getYesNoOther()) {
                    case "1":
                        viewHolder.smsbody.setText(replyStateInfo.getReplyContent());
                        viewHolder.name.setText(replyStateInfo.getExpertName());
                        viewHolder.date.setText(time);
                        viewHolder.phoneNumber.setText(replyStateInfo.getTel());
                        viewHolder.code.setText("(" + replyStateInfo.getExpertCode() + ")");
                        viewHolder.state.setText("Y");
                        break;
                    case "2":
                        viewHolder.smsbody.setText(replyStateInfo.getReplyContent());
                        viewHolder.name.setText(replyStateInfo.getExpertName());
                        viewHolder.date.setText(time);
                        viewHolder.phoneNumber.setText(replyStateInfo.getTel());
                        viewHolder.code.setText("(" + replyStateInfo.getExpertCode() + ")");
                        viewHolder.state.setText("N");
                        break;
                    case "3":
                        viewHolder.smsbody.setText(replyStateInfo.getReplyContent());
                        viewHolder.name.setText(replyStateInfo.getExpertName());
                        viewHolder.date.setText(time);
                        viewHolder.phoneNumber.setText(replyStateInfo.getTel());
                        viewHolder.code.setText("(" + replyStateInfo.getExpertCode() + ")");
                        viewHolder.state.setText("O");
                        break;
                }
                break;
            case "2":
                viewHolder.smsbody.setText(replyStateInfo.getReplyContent());
                viewHolder.name.setText("未知");
                viewHolder.date.setText(time);
                viewHolder.phoneNumber.setText(replyStateInfo.getTel());
                viewHolder.code.setText("(未知)");
                viewHolder.state.setText("无法匹配");
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.code)
        TextView code;
        @BindView(R.id.state)
        TextView state;
        @BindView(R.id.phoneNumber)
        TextView phoneNumber;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.smsbody)
        TextView smsbody;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
