package com.example.multi_vendor.common

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multi_vendor.adapters.MessageAdapter
import com.example.multi_vendor.databinding.ActivityViewMessagesBinding
import com.example.multi_vendor.models.Message
import com.example.multi_vendor.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewMessages : AppCompatActivity() {

    private lateinit var binding : ActivityViewMessagesBinding
    private lateinit var friendName : String
    private lateinit var roomId : String
    private lateinit var messages : ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setChatRoom()

        messages = ArrayList()

        binding.messagesRecycler.layoutManager = LinearLayoutManager(this)
        binding.messagesRecycler.adapter = MessageAdapter(messages)

        friendName = intent.getStringExtra("nameOfMyFriend").toString()
        val friendEmail = intent.getStringExtra("emailOfMyFriend").toString()
        binding.chattingWith.text = friendName

        binding.send.setOnClickListener {
            if (binding.typeMessage.text.toString().isEmpty()) {
                return@setOnClickListener
            }
            FirebaseDatabase.getInstance().getReference("Messages/$roomId").push().setValue(
                Message(
                    sender = FirebaseAuth.getInstance().currentUser!!.email!!,
                    receiver = friendEmail,
                    content = binding.typeMessage.text.toString()
                )
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.typeMessage.setText("")
                    binding.typeMessage.clearFocus()
                } else {
                    Toast.makeText(this , task.exception!!.localizedMessage , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setChatRoom() {
        FirebaseDatabase.getInstance().getReference("App users/${FirebaseAuth.getInstance().currentUser!!.uid}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val myName = snapshot.getValue(User::class.java)!!.name
                    roomId = if (friendName > myName) {
                        myName + friendName
                    } else if (friendName.compareTo(myName) == 0) {
                        myName + friendName
                    } else {
                        friendName + myName
                    }
                    messageListener(roomId)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun messageListener(roomId: String) {
        FirebaseDatabase.getInstance().getReference("Messages/${roomId}").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                snapshot.children.forEach { dataSnapshot ->
                    messages.add(dataSnapshot.getValue(Message::class.java)!!)
                }
                binding.messagesRecycler.setHasFixedSize(true)
                binding.messagesRecycler.scrollToPosition(messages.size - 1)
                binding.progress4.visibility = View.GONE
                binding.messagesRecycler.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}