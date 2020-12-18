package com.mamp.qrscanner.listView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mamp.qrscanner.R;

import java.util.ArrayList;
import java.util.List;

public class QrDataListViewAdapter extends BaseAdapter implements Filterable {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList. (원본 데이터 리스트)
    private List<QrDataListViewItem> originalItemList;
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private List<QrDataListViewItem> filteredItemList;
    private Context context;
    private Filter listFilter;

    public QrDataListViewAdapter(Context context, List<QrDataListViewItem> qrDataList) {
        this.originalItemList = qrDataList;
        this.filteredItemList = originalItemList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.qr_data_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = convertView.findViewById(R.id.qr_data_icon) ;
        TextView qrDataTextView =  convertView.findViewById(R.id.qr_data) ;
        TextView dateTextView = convertView.findViewById(R.id.qr_data_date) ;

        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        QrDataListViewItem listViewItem = filteredItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getQrDataIcon());
        qrDataTextView.setText(listViewItem.getQrData());
        dateTextView.setText(listViewItem.getQrDataDate());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if( listFilter == null) {
            listFilter = new ListFilter();
        }

        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;
            String filterString = constraint.toString().toUpperCase();

            results.values = originalItemList ;
            ArrayList<QrDataListViewItem> itemList = new ArrayList<>() ;

            for (QrDataListViewItem item : originalItemList) {
                if (item.getQrData().toUpperCase().contains(filterString) ||
                        item.getQrDataDate().toUpperCase().contains(filterString))
                {
                    itemList.add(item) ;
                }
            }

            results.values = itemList ;
            results.count = itemList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // update listview by filtered data list.
            filteredItemList = (ArrayList<QrDataListViewItem>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }
}
