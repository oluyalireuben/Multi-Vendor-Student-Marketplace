package com.example.multi_vendor.common

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multi_vendor.adapters.FriendsAdapter
import com.example.multi_vendor.databinding.FragmentChatsBinding
import com.example.multi_vendor.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatsFragment : Fragment() {
   private lateinit var binding : FragmentChatsBinding
    private lateinit var users : ArrayList<User>
    private lateinit var onFriendClick: FriendsAdapter.OnFriendClick

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        users = ArrayList()
        onFriendClick = object : FriendsAdapter.OnFriendClick {
            override fun onFriendClicked(position: Int) {
                startActivity(
                    Intent(requireContext() , ViewMessages::class.java)
                    .putExtra("emailOfMyFriend", users[position].email)
                    .putExtra("nameOfMyFriend", users[position].name)
                )
            }

        }
        binding.recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = layoutManager

        FirebaseDatabase.getInstance().getReference("App users").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                snapshot.children.forEach { dataSnapshot ->
                    if (FirebaseAuth.getInstance().currentUser!!.email != dataSnapshot.getValue(User::class.java)!!.email) {
                        dataSnapshot.getValue(User::class.java)?.let { users.add(it) }
                    }
                }
                binding.progress3.visibility = View.GONE
                binding.recycler.visibility = View.VISIBLE
                binding.recycler.adapter = FriendsAdapter(users = users , onFriendClick = onFriendClick)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}