package com.lahrachtech.messanger.fragmets


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.lahrachtech.messanger.ChatActivity
import com.lahrachtech.messanger.ProfileActivity
import com.lahrachtech.messanger.R
import com.lahrachtech.messanger.model.TextMessage
import com.lahrachtech.messanger.model.User
import com.lahrachtech.messanger.recyclerview.ChatItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : Fragment() {

    private lateinit var fragmentContext: Context

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var chatSection: Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

//        val view =

//        view.search_image_view.setOnClickListener {
//
//            requireActivity().startActivity(Intent(activity, SearchActivity::class.java))
//        }

        val textViewTitle = activity?.findViewById(R.id.title_toolbar_textView) as TextView
        textViewTitle.text = resources.getText(R.string.chat)

        val circleImageViewProfileImage =
            activity?.findViewById(R.id.circleImageView_profile_image) as ImageView

        circleImageViewProfileImage.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
            requireActivity().finish()
        }

        // Listening of chats.....
        addChatListener(::initRecyclerView)

        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    private fun addChatListener(onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach { document ->
                    if (
                        document.exists()
                        && document.id != FirebaseAuth.getInstance().currentUser!!.uid
                    ) {
                        items.add(
                            ChatItem(
                                document.id,
                                document.toObject(User::class.java)!!,
//                                document.toObject(TextMessage::class.java)!!,
                                fragmentContext
                            )
                        )
                    }

                }
                onListen(items)
            }
    }

    private fun initRecyclerView(item: List<Item>) {
        chat_recyclerView_fragment.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                chatSection = Section(item)
                add(chatSection)
                setOnItemClickListener(onItemClick)
            }
        }
    }

    private val onItemClick = OnItemClickListener { item, view ->
        if (item is ChatItem) {
            val intentChatActivity = Intent(activity, ChatActivity::class.java)
            intentChatActivity.putExtra("user_name", item.user.name)
            intentChatActivity.putExtra("profile_image", item.user.profileImage)
            intentChatActivity.putExtra("other_uid", item.uid)
            requireActivity().startActivity(intentChatActivity)
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }


}
