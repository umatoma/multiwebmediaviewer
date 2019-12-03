package io.github.umatoma.multiwebmediaviewer.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.hatenaAuth.HatenaAuthActivity
import io.github.umatoma.multiwebmediaviewer.hatenaEntry.HatenaEntryActivity
import kotlinx.android.synthetic.main.fragment_hatena_entry_list.*
import kotlinx.android.synthetic.main.fragment_hatena_entry_list.btnHatenaSignIn

class HatenaEntryListFragment : Fragment() {

    fun getTitle(context: Context): String {
        return context.getString(R.string.fragment_hatena_entry_list_title)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hatena_entry_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val homeViewModelFactory = HomeViewModel.Factory(requireContext())
        val homeViewModel = ViewModelProviders
            .of(requireActivity(), homeViewModelFactory)
            .get(HomeViewModel::class.java)

        val entryListAdapter = HatenaEntryListAdapter(
            onClickItem = { entry ->
                HatenaEntryActivity.startActivity(requireContext(), entry)
            }
        )

        recyclerViewHatenaEntryList.also {
            val divider = DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
            it.setHasFixedSize(true)
            it.addItemDecoration(divider)
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = entryListAdapter
        }

        btnHatenaSignIn.setOnClickListener {
            startActivity(Intent(requireContext(), HatenaAuthActivity::class.java))
        }

        homeViewModel.hatenaEntryListLiveData.observe(viewLifecycleOwner, Observer {
            entryListAdapter.setItemList(it)
        })

        homeViewModel.isSignedInHatenaLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                recyclerViewHatenaEntryList.visibility = View.VISIBLE
                layoutHatenaSignIn.visibility = View.GONE
            } else {
                recyclerViewHatenaEntryList.visibility = View.GONE
                layoutHatenaSignIn.visibility = View.VISIBLE
            }
        })

        homeViewModel.fetchHatenaEntryList()
    }
}
