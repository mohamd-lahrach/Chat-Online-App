package com.lahrachtech.messanger.fragmets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lahrachtech.messanger.R

class DiscoverFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tvTitle = activity?.findViewById(R.id.title_toolbar_textView) as TextView
        tvTitle.text = resources.getText(R.string.discover)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dicover, container, false)
    }

}