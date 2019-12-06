package io.github.umatoma.multiwebmediaviewer.view.home


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.github.umatoma.multiwebmediaviewer.R


class FeedlyEntryListFragment : Fragment() {

    fun getTitle(context: Context): String {
        return context.getString(R.string.fragment_feedly_entry_list_title)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedly_entry_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}