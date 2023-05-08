package com.example.bookapp

import android.widget.Filter
import com.example.bookapp.adapter.AdapterPdfUser
import com.example.bookapp.model.ModelPdf

class FilterPdfUser : Filter {

    val filterList: ArrayList<ModelPdf>

    var adapterPdfUser: AdapterPdfUser

    constructor(filterList: ArrayList<ModelPdf>, adapterPdfUser: AdapterPdfUser) : super() {
        this.filterList = filterList
        this.adapterPdfUser = adapterPdfUser
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        var constraint: CharSequence? = constraint
        val results = FilterResults()
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

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        //apply filter changes
        adapterPdfUser.pdfArrayList = results.values as ArrayList<ModelPdf>

        //notify changes
        adapterPdfUser.notifyDataSetChanged()
    }
}