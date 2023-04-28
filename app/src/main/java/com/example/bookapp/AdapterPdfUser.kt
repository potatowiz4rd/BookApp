package com.example.bookapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.databinding.RowPdfUserBinding
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.storage.FirebaseStorage

class AdapterPdfUser : RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> {

    private var context: Context

    private var pdfArrayList: ArrayList<ModelPdf>

    private lateinit var binding: RowPdfUserBinding

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfUser {
        //inflate layout row_pdf_user
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderPdfUser(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfUser, position: Int) {
        val model = pdfArrayList[position]
        val bookId = model.id
        val title = model.title
        val url = model.url
        val image = model.image

        //val date = MyApplication.formatTimeStamp(timestamp)
        //holder.title.text = title

        //MyApplication.loadPdfFromUrlSinglePage(url, title, holder.pdfView, holder.progressBar, null)

        MyApplication.loadPdfThumbnail(url, title, image, holder.thumbnail, holder.progressBar)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra("bookId", bookId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    inner class HolderPdfUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail = binding.pdfThumbnail
        val progressBar = binding.progressBar
        //val title = binding.titleTv
    }

}