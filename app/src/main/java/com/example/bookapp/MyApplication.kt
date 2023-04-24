package com.example.bookapp

import android.app.Application
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import java.util.*
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        fun formatTimeStamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }
    }

    fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView) {
        val TAG = "PDF_SIZE_TAG"
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        ref.metadata.addOnSuccessListener { storageMetadata ->
            Log.d(TAG, "loadPdfSize: got metadata")
            val bytes = storageMetadata.sizeBytes.toDouble()
            Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

            val kb = bytes / 1024
            val mb = kb / 1024
            if (mb >= 1) {
                sizeTv.text = "${String.format("$.2f", mb)} MB"
            } else if (kb >= 1) {
                sizeTv.text = "${String.format("$.2f", kb)} KB"
            } else {
                sizeTv.text = "${String.format("$.2f", bytes)} bytes"
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "loadPdfSize: failed to get metadata due to ${e.message}")
        }
    }

    fun loadPdfFromUrlSinglePage(
        pdfUrl: String,
        pdfTitle: String,
        pdfView: PDFView,
        pagesTv: TextView?
    ) {
        val TAG = "PDF_THUMBNAIL_TAG"

        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        ref.getBytes(Constants.MAX_BYTES_PDF).addOnSuccessListener { bytes ->
            Log.d(TAG, "loadPdfSize: got metadata")
            Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

            //set to pdfview
            pdfView.fromBytes(bytes).pages(0).spacing(0).swipeHorizontal(true).enableSwipe(true)
                .onError { t ->
                    Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                }.onPageError { page, t ->
                    Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                }.onLoad { nbPages ->
                    if (pagesTv != null) {
                        //set page numbers
                        pagesTv.text = "$nbPages"
                    }
                }
        }.addOnFailureListener { e ->
            Log.d(TAG, "loadPdfSize: failed to get metadata due to ${e.message}")
        }
    }

    fun loadCategory(categoryId: String, categoryTv: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(categoryId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get category name
                val category = "${snapshot.child("category").value}"

                //set category
                categoryTv.text = category
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}