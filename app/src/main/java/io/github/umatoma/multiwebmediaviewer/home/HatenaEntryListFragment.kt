package io.github.umatoma.multiwebmediaviewer.home

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
import kotlinx.android.synthetic.main.fragment_hatena_entry_list.*

class HatenaEntryListFragment : Fragment() {

    private val viewModel by lazy {
        val viewModelFactory = HatenaEntryListViewModel
            .Factory(requireContext())
        ViewModelProviders
            .of(this, viewModelFactory)
            .get(HatenaEntryListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hatena_entry_list, container, false)
    }

    override fun onResume() {
        super.onResume()

        viewModel.fetchIsSignedIn()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val entryListAdapter = HatenaEntryListAdapter()

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

        viewModel.isSignedInAndHasChangedLiveData.observe(viewLifecycleOwner, Observer { (isSignedIn, hasChanged) ->
            if (isSignedIn) {
                layoutHatenaSignIn.visibility = View.GONE
                recyclerViewHatenaEntryList.visibility = View.VISIBLE

                if (hasChanged) {
                    viewModel.fetchEntryList()
                }
            } else {
                layoutHatenaSignIn.visibility = View.VISIBLE
                recyclerViewHatenaEntryList.visibility = View.GONE

                if (hasChanged) {
                    viewModel.clearEntryList()
                }
            }
        })
        viewModel.entryListLiveData.observe(viewLifecycleOwner, Observer {
            entryListAdapter.setItemList(it)
        })
    }
}
