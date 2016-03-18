package cn.zhenghongen.android.app.pinnedheadergrouplistview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.zhenghongen.android.app.libary.PinnedHeaderListView;
import cn.zhenghongen.android.app.libary.SideBar;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private TextView filter;
    private PinnedHeaderListView listView;
    private SideBar sideBar;
    private TextView dialog;

    private SectionedAdapter adapter;

    private String[] sectionArray = SideBar.b;
    private Map<String, Integer> sectionCountMap = this.getSectionCount();
    private List<DataModel> list = this.getDataList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    Log.d(TAG, "Side s:" + s + "\tposition:" + position);
                    listView.setSelection(position);
                }
            }
        });


        int listLen = list.size();
        adapter = new SectionedAdapter(this, sectionArray, sectionCountMap, list);

        listView = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
        listView.setAdapter(adapter);

        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /*LinearLayout header1 = (LinearLayout) inflator.inflate(R.layout.list_item, null);
        ((TextView) header1.findViewById(R.id.textItem)).setText("HEADER 1");
        LinearLayout header2 = (LinearLayout) inflator.inflate(R.layout.list_item, null);
        ((TextView) header2.findViewById(R.id.textItem)).setText("HEADER 2");*/
        LinearLayout footer = (LinearLayout) inflator.inflate(R.layout.list_footer, null);
        ((TextView) footer.findViewById(R.id.textItem)).setText(listLen + "位联系人");
     /*   listView.addHeaderView(header1);
        listView.addHeaderView(header2);*/
        listView.addFooterView(footer);

        //搜索
        filter = (TextView) findViewById(R.id.filter_edit);
        filter.setHint("搜索" + listLen + "位联系人");
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                if (TextUtils.isEmpty(s)) {
                    adapter.updateListView(sectionArray, sectionCountMap, list);
                    sideBar.setVisibility(View.VISIBLE);
                } else {
                    sideBar.setVisibility(View.GONE);
                    filterData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        String[] filterSectionArray = null;
        Map<String, Integer> filterSectionCountMap = new HashMap<String, Integer>();
        List<DataModel> filterDateList = new ArrayList<DataModel>();
        for (DataModel dataModel : list) {
            String name = dataModel.getName();
            if (name.indexOf(filterStr.toString()) != -1) {
                filterDateList.add(dataModel);

                String key = dataModel.getSortLetters();
                if (filterSectionCountMap.get(key) == null) {
                    filterSectionCountMap.put(key, 0);
                }

                int count = filterSectionCountMap.get(key);
                filterSectionCountMap.put(key, count + 1);
            }
        }

        filterSectionArray = new String[filterSectionCountMap.size()];
        Set<String> keySet = filterSectionCountMap.keySet();
        filterSectionArray = (String[]) keySet.toArray(filterSectionArray);

        adapter.updateListView(filterSectionArray, filterSectionCountMap, filterDateList);
    }

    private Map<String, Integer> getSectionCount() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < SideBar.b.length; i++) {
            map.put(SideBar.b[i], i + 1);
        }
        return map;
    }

    private List<DataModel> getDataList() {
        List<DataModel> list = new ArrayList<>();

        Map<String, Integer> map = this.sectionCountMap;
        for (int i = 0; i < SideBar.b.length; i++) {
            String key = SideBar.b[i];
            for (int j = 0; j < map.get(key); j++) {
                DataModel data = new DataModel();
                data.setName(key + j);
                data.setSortLetters(key);
                list.add(data);
                Log.d("DataModel", "key:" + key + "\tname:" + data.getName() + "\tsl:" + data.getSortLetters());
            }
        }


        return list;
    }
}
