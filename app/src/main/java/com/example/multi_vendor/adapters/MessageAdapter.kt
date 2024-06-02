package com.example.multi_vendor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.multi_vendor.models.Message
import com.example.multi_vendor.R
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter (
    private val messages : ArrayList<Message>
): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){

    inner class MessageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val constraintLayout : ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
        val messageContent : TextView = itemView.findViewById(R.id.messageContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_holder, parent , false))
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.messageContent.text = messages[position].content
        val constraintLayout = holder.constraintLayout

        if (messages[position].sender == FirebaseAuth.getInstance().currentUser!!.email) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.clear(R.id.messageContent, ConstraintSet.LEFT)
            constraintSet.connect(
                R.id.messageContent,
                ConstraintSet.RIGHT,
                R.id.constraintLayout,
                ConstraintSet.RIGHT
            )
            constraintSet.applyTo(constraintLayout)
        } else {
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.clear(R.id.messageContent, ConstraintSet.RIGHT)
            constraintSet.connect(
                R.id.messageContent,
                ConstraintSet.LEFT,
                R.id.constraintLayout,
                ConstraintSet.LEFT
            )
            constraintSet.applyTo(constraintLayout)
        }
    }
}









