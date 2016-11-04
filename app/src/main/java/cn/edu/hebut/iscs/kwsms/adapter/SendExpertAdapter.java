package cn.edu.hebut.iscs.kwsms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.hebut.iscs.kwsms.R;
import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;

/**
 * Created by lixueyang on 16-8-29.
 */
public class SendExpertAdapter extends ArrayAdapter<ExpertInfo> {
    private static final String TAG ="SendExpertAdapter" ;
    private Context mContext;
    private LayoutInflater mInflater;
    private  List<ExpertInfo> list = new ArrayList<ExpertInfo>();   //存储选中的项
    public  List<Boolean> stateList = new ArrayList<Boolean>();

    public SendExpertAdapter(Context context) {
        super(context, R.layout.item_for_send_exper);
        this.mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_for_send_exper, parent,
                    false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ExpertInfo expertInfo = getItem(position);
        viewHolder.textViewSmsId.setText(expertInfo.getId() + "");
        viewHolder.textViewNameCode.setText(expertInfo.getName() + "(" + expertInfo.getExpertCode() + ")");
        viewHolder.textViewPhone.setText(expertInfo.getTel());
        viewHolder.textViewSmsMansege.setText("\t\t"+expertInfo.getMsgContent());
        viewHolder.checkBoxDelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {        //为选择状态
                    stateList.set(position, true);
                    list.add(expertInfo);
                } else {
                    stateList.set(position, false);
                    list.remove(expertInfo);
                }
            }
        });
        if(position>=stateList.size()){
            stateList.add(position,false);
        }
        if (stateList.get(position)) {
            viewHolder.checkBoxDelect.setChecked(true);
        } else {
            viewHolder.checkBoxDelect.setChecked(false);
        }
        return convertView;
    }


    /**
     * 清空List
     */
    public void clearList(){
        list.clear();
        stateList.clear();
    }

    public List<ExpertInfo> getList() {
        //对list去重复操作后再返回
        for(int i=0;i<list.size();i++){
            for(int j=list.size()-1;j>i;j--){
                if(list.get(i).getExpertCode().equals(list.get(j).getExpertCode())){
                    list.remove(j);
                }
            }
        }
        Log.d(TAG,"选中且即将发送的个数"+list.size());
        return list;
    }

    public void setList(List<ExpertInfo> list) {
        this.list = list;
    }

    public List<Boolean> getStateList() {
        return stateList;
    }

    public void setStateList(List<Boolean> stateList) {
        this.stateList = stateList;
    }

    static class ViewHolder {
        @BindView(R.id.textView_sms_id)
        TextView textViewSmsId;
        @BindView(R.id.textView_name_code)
        TextView textViewNameCode;
        @BindView(R.id.textView_phone)
        TextView textViewPhone;
        @BindView(R.id.checkBox_delect)
        CheckBox checkBoxDelect;
        @BindView(R.id.textView_sms_mansege)
        TextView textViewSmsMansege;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
