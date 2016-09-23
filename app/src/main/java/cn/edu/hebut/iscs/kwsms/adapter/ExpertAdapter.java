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
import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;


public class ExpertAdapter extends ArrayAdapter<ExpertInfo> {
    private Context mContext;
    private LayoutInflater mInflater;

    public ExpertAdapter(Context context) {
        super(context, R.layout.item_for_expert);
        this.mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_for_expert, parent,
                    false);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ExpertInfo expertInfo = getItem(position);
        viewHolder.expertId.setText(expertInfo.getId() + "");
        viewHolder.expertCode.setText(expertInfo.getExpertCode());
        viewHolder.expertName.setText(expertInfo.getName());
        viewHolder.expertPhone.setText(expertInfo.getTel());
        viewHolder.expertContent.setText(expertInfo.getMsgContent());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.expert_id)
        TextView expertId;
        @BindView(R.id.expert_code)
        TextView expertCode;
        @BindView(R.id.expert_name)
        TextView expertName;
        @BindView(R.id.expert_phone)
        TextView expertPhone;
        @BindView(R.id.expert_content)
        TextView expertContent;
        @BindView(R.id.expert_status)
        TextView expertStatus;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
