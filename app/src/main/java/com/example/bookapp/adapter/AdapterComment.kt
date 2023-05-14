package com.example.bookapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.MyApplication
import com.example.bookapp.R
import com.example.bookapp.databinding.RowCommentsBinding
import com.example.bookapp.model.ModelComment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class AdapterComment : RecyclerView.Adapter<AdapterComment.HolderComment> {

    val context: Context

    val commentArrayList: ArrayList<ModelComment>

    private lateinit var binding: RowCommentsBinding

    private lateinit var firebaseAuth: FirebaseAuth

    constructor(context: Context, commentArrayList: ArrayList<ModelComment>) {
        this.context = context
        this.commentArrayList = commentArrayList

        firebaseAuth = FirebaseAuth.getInstance()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComment {
        binding = RowCommentsBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderComment(binding.root)
    }

    override fun onBindViewHolder(holder: HolderComment, position: Int) {
        //get data, set data
        val model = commentArrayList[position]

        val id = model.id
        val bookId = model.bookId
        val comment = model.comment
        val uid = model.uid
        val timestamp = model.timestamp

        //format timestamp
        val date = MyApplication.formatTimeStamp(timestamp.toLong())

        //set data
        holder.dateTv.text = date
        holder.commentTv.text = comment

        loadUserDetails(model, holder)

        //handle delete
        holder.itemView.setOnClickListener {
            //check user
            if (firebaseAuth.currentUser != null && firebaseAuth.uid === uid) {
                deleteCommentDialog(model, holder)
            }
        }
    }

    private fun deleteCommentDialog(model: ModelComment, holder: AdapterComment.HolderComment) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Comment").setMessage("Are you sure want to delete this comment?")
            .setPositiveButton("Delete") { d, e ->
                val bookId = model.bookId
                val commentId = model.id
                //delete comment
                val ref = FirebaseDatabase.getInstance().getReference("Books")
                ref.child(model.bookId).child("Comments").child(commentId)
                    .removeValue().addOnSuccessListener {
                        Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT)
                            .show()
                    }
            }.setNegativeButton("Cancel") { d, e ->
                d.dismiss()
            }.show()
    }

    private fun loadUserDetails(model: ModelComment, holder: AdapterComment.HolderComment) {
        val uid = model.uid
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = "${snapshot.child("name").value}"
                val profileImg = "${snapshot.child("profileImg").value}"

                //set data
                holder.nameTv.text = name
                try {
                    Picasso.get().load(profileImg).placeholder(R.drawable.circle_user).fit()
                        .centerCrop().into(holder.profileIv);
                } catch (e: Exception) {
                    //check null
                    holder.profileIv.setImageResource(R.drawable.circle_user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getItemCount(): Int {
        return commentArrayList.size
    }

    inner class HolderComment(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileIv = binding.profileIV
        val nameTv = binding.nameTv
        val dateTv = binding.dateTv
        val commentTv = binding.commentTv
    }

}