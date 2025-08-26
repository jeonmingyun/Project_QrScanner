package com.mamp.qrscanner.listView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.mamp.qrscanner.R
import java.util.Locale

class QrDataListViewAdapter(context: Context?, qrDataList: MutableList<QrDataListViewItem?>) :
    BaseAdapter(), Filterable {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList. (원본 데이터 리스트)
    private val originalItemList: MutableList<QrDataListViewItem>

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private var filteredItemList: MutableList<QrDataListViewItem>
    private val context: Context?
    private var listFilter: Filter? = null

    init {
        this.originalItemList = qrDataList as MutableList<QrDataListViewItem>
        this.filteredItemList = originalItemList
        this.context = context
    }

    override fun getCount(): Int {
        return filteredItemList.size
    }

    override fun getItem(position: Int): Any? {
        return filteredItemList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        var convertView = convertView
        if (convertView == null) {
            val inflater = parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.qr_data_listview_item, parent, false)
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        val iconImageView = convertView.findViewById<ImageView>(R.id.qr_data_icon)
        val qrDataTextView = convertView.findViewById<TextView>(R.id.qr_data)
        val dateTextView = convertView.findViewById<TextView>(R.id.qr_data_date)

        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem: QrDataListViewItem = filteredItemList.get(position)

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.qrDataIcon)
        qrDataTextView.setText(listViewItem.qrData)
        dateTextView.setText(listViewItem.qrDataDate)

        return convertView
    }

    override fun getFilter(): Filter {
        if (listFilter == null) {
            listFilter = ListFilter()
        }

        return listFilter!!
    }

    private inner class ListFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            val filterString = constraint.toString().uppercase(Locale.getDefault())

            results.values = originalItemList
            val itemList: ArrayList<QrDataListViewItem?> = ArrayList<QrDataListViewItem?>()

            for (item in originalItemList) {
                if(item.qrData == null)
                    break

                if (item.qrData!!.uppercase().contains(filterString) ||
                    item.qrDataDate!!.uppercase().contains(filterString)
                ) {
                    itemList.add(item)
                }
            }

            results.values = itemList
            results.count = itemList.size

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            // update listview by filtered data list.
            filteredItemList = results.values as ArrayList<QrDataListViewItem>

            // notify
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
}
