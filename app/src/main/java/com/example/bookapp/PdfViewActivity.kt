package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.example.bookapp.databinding.ActivityPdfViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.abs

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    private var isNightMode = false // variable to keep track of the current mode
    private var isSwipeVertical = true

    private val setSwipeVerticalMethod by lazy {
        binding.pdfView.javaClass.getDeclaredMethod("setSwipeVertical", Boolean::class.java)
    }

    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    var bookId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent
        bookId = intent.getStringExtra("bookId")!!
        loadBookDetails()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.changeThemeBtn.setOnClickListener {
            isNightMode = !isNightMode // toggle the mode
            binding.pdfView.setNightMode(isNightMode) // apply the new mode
        }

        binding.changeOrientationBtn.setOnClickListener {
            isSwipeVertical = !isSwipeVertical // toggle the swipe mode
            try {
                setSwipeVerticalMethod.isAccessible = true
                setSwipeVerticalMethod.invoke(binding.pdfView, isSwipeVertical)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.pageSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Update the current page of the PDFView when the user slides the thumb
                    binding.pdfView.jumpTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

    }

    private fun loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Get Pdf URL from db")

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pdfUrl = snapshot.child("url").value
                Log.d(TAG, "onDataChange: PDF_URL: $pdfUrl")

                loadBookFromUrl("$pdfUrl")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadBookFromUrl(pdfUrl: String) {
        Log.d(TAG, "loadBookFromUrl: Get Pdf from storage")

        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Constants.MAX_BYTES_PDF).addOnSuccessListener { bytes ->
            Log.d(TAG, "loadBookFromUrl: got pdf from urf!")
            binding.pdfView.fromBytes(bytes).swipeHorizontal(true).pageSnap(true).pageFling(true)
                .enableAnnotationRendering(true)
                .nightMode(false)
                .onPageChange { page, pageCount ->
                    //set current page and total page
                    binding.pageSlider.max = pageCount - 1
                    val currentPage = page + 1
                    binding.pageCountTv.text = "$currentPage/$pageCount"
                }.onError { t ->
                    Log.d(TAG, "loadBookFromUrl: ${t.message}")
                }.onPageError { page, t ->
                    Log.d(TAG, "loadBookFromUrl: ${t.message}")
                }.load()
            binding.progressBar.visibility = View.GONE
        }.addOnFailureListener { e ->
            Log.d(TAG, "loadBookFromUrl: Failed to get url due to ${e.message}")
            binding.progressBar.visibility = View.GONE
        }
    }
}