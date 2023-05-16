package com.example.bookapp.filter

import android.widget.Filter
import com.example.bookapp.adapter.AdapterPdfSearch
import com.example.bookapp.model.ModelPdf

class FilterPdfSearch : Filter {

    val filterList: ArrayList<ModelPdf>

    var adapterPdfSearch: AdapterPdfSearch

    constructor(filterList: ArrayList<ModelPdf>, adapterPdfSearch: AdapterPdfSearch) : super() {
        this.filterList = filterList
        this.adapterPdfSearch = adapterPdfSearch
    }

    override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
        var constraint: CharSequence? = constraint
        val results = Filter.FilterResults()
        //check empty
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filteredModels = ArrayList<ModelPdf>()
            for (i in filterList.indices) {
                if (filterList[i].title.uppercase().contains(constraint)) {
                    //search value matched with title, add to list
                    filteredModels.add(filterList[i])
                }
            }
            //return list and size
            results.count = filteredModels.size
            results.values = filteredModels
        } else {
            //return original
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
        //apply filter changes
        adapterPdfSearch.pdfArrayList = results.values as ArrayList<ModelPdf>

        //notify changes
        adapterPdfSearch.notifyDataSetChanged()
    }
}