package com.example.bookapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.BookDetailActivity
import com.example.bookapp.filter.FilterPdfSearch
import com.example.bookapp.MyApplication
import com.example.bookapp.databinding.RowPdfSearchBinding
import com.example.bookapp.model.ModelPdf

class AdapterPdfSearch : RecyclerView.Adapter<AdapterPdfSearch.HolderPdfSearch>, Filterable {

    private var context: Context

    public var pdfArrayList: ArrayList<ModelPdf>
    public var filterList: ArrayList<ModelPdf>

    private lateinit var binding: RowPdfSearchBinding

    private var filter: FilterPdfSearch? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfSearch {
        //inflate layout row_pdf_user
        binding = RowPdfSearchBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderPdfSearch(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfSearch, position: Int) {
        val model = pdfArrayList[position]
        val bookId = model.id
        val title = model.title
        val url = model.url
        val author = model.author
        val categoryId = model.categoryId
        val image = model.image
        val audio = model.audio

        //val date = MyApplication.formatTimeStamp(timestamp)
        holder.title.text = title
        holder.author.text = author

        //MyApplication.loadPdfFromUrlSinglePage(url, title, holder.pdfView, holder.progressBar, null)

        MyApplication.loadPdfThumbnail(
            url,
            audio,
            title,
            author,
            image,
            holder.thumbnail,
            holder.progressBar
        )

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra("bookId", bookId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    inner class HolderPdfSearch(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail = binding.pdfThumbnail
        val progressBar = binding.progressBar
        val title = binding.titleTv
        val author = binding.authorTv
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterPdfSearch(filterList, this)
        }
        return filter as FilterPdfSearch
    }
}