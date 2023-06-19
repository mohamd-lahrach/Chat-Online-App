package com.lahrachtech.messanger.fragmets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lahrachtech.messanger.R
import kotlinx.android.synthetic.main.activity_main.*

class PeopleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tvTitle = activity?.findViewById(R.id.title_toolbar_textView) as TextView
        tvTitle.text = resources.getText(R.string.people)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false)
    }
}