package cn.zhenghongen.android.app.pinnedheadergrouplistview;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.zhenghongen.android.app.libary.SectionedBaseAdapter;

/**
 * Created by ZhengHongEn on 2016/3/16.
 */
public class SectionedAdapter extends SectionedBaseAdapter implements SectionIndexer {
    public static final String TAG = "SectionedAdapter";
    private String[] sectionArry = null;
    private Map<String, Integer> sectionCountMap = null;
    private Integer[] sectionIndexArray = null;
    private List<DataModel> list = null;

    private Context mContext;

    public SectionedAdapter(Context mContext, String[] sectionArry, Map<String, Integer> sectionCountMap, List<DataModel> dataList) {
        this.mContext = mContext;
        this.sectionArry = sectionArry;
        this.sectionCountMap = sectionCountMap;
        this.list = dataList;
        calSectionIndex();
    }

    public void updateListView(String[] sectionArry, Map<String, Integer> sectionCountMap, List<DataModel> dataList) {
        this.sectionArry = sectionArry;
        this.sectionCountMap = sectionCountMap;
        this.list = dataList;
        calSectionIndex();

        this.notifyDataSetChanged();
    }

    /**
     * 计算分组索引
     */
    private void calSectionIndex() {
        //计算分组索引
        this.sectionIndexArray = new Integer[sectionArry.length];
        for (int i = 0; i < sectionArry.length; i++) {
            if (i == 0) {
                sectionIndexArray[i] = 0;
            } else {
                sectionIndexArray[i] = sectionIndexArray[i - 1] + sectionCountMap.get(sectionArry[i - 1]);
            }
            Log.d(TAG, "sectionIndexArray sectionName:" + sectionArry[i] + "\tindex:" + sectionIndexArray[i]);
        }
    }


    @Override
    public Object getItem(int section, int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSectionCount() {
        int count = 0;
        if (sectionArry != null) {
            count = sectionArry.length;
        }
        return count;
    }

    @Override
    public int getCountForSection(int section) {
        int count = 0;
        if (sectionArry != null && sectionCountMap != null) {
            count = sectionCountMap.get(sectionArry[section]);
        }
        return count;
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        final View view;
        final ItemViewHolder viewHolder;
        //优化性能，防止快速滚动时产生性能瓶颈
        if (convertView == null) {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.list_item, null);
            viewHolder = new ItemViewHolder();
            viewHolder.mName = (TextView) view.findViewById(R.id.textItem);
            view.setTag(viewHolder);//将ViewHolder存储在view中
        } else {
            view = convertView;
            viewHolder = (ItemViewHolder) view.getTag();
        }

        int sectionIndex = sectionIndexArray[section];
        viewHolder.mName.setText(list.get(sectionIndex + position).getName());
        return view;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        final View view;
        final SectionViewHolder viewHolder;
        //优化性能，防止快速滚动时产生性能瓶颈
        if (convertView == null) {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.header_item, null);
            viewHolder = new SectionViewHolder();
            viewHolder.mSectionName = (TextView) view.findViewById(R.id.textItem);
            view.setTag(viewHolder);//将ViewHolder存储在view中
        } else {
            view = convertView;
            viewHolder = (SectionViewHolder) view.getTag();
        }

        viewHolder.mSectionName.setText(sectionArry[section]);
        return view;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < sectionArry.length; i++) {
            char firstChar = sectionArry[i].toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return sectionIndexArray[i] + i; //需加上所在的header数据的位置当偏移量
            }
        }

        return -1;
    }

    class ItemViewHolder {
        TextView mName;
    }

    class SectionViewHolder {
        TextView mSectionName;
    }
}
