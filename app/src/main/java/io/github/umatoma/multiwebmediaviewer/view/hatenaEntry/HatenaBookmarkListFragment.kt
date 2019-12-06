package io.github.umatoma.multiwebmediaviewer.view.hatenaEntry


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry
import io.github.umatoma.multiwebmediaviewer.viewModel.hatenaEntry.HatenaBookmarkListViewModel
import kotlinx.android.synthetic.main.fragment_hatena_bookmark_list.*


class HatenaBookmarkListFragment : Fragment() {

    companion object {

        private const val KEY_ENTRY= "KEY_ENTRY"

        fun newInstance(entry: HatenaEntry): HatenaBookmarkListFragment {
            val bundle = Bundle().also {
                it.putSerializable(KEY_ENTRY, entry)
            }
            return HatenaBookmarkListFragment().also {
                it.arguments = bundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hatena_bookmark_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val entry = requireArguments().getSerializable(KEY_ENTRY) as HatenaEntry
        val bookmarkListAdapter = HatenaBookmarkListAdapter(requireContext())

        val viewModelFactory = HatenaBookmarkListViewModel.Factory(requireContext(), entry)
        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HatenaBookmarkListViewModel::class.java)

        recyclerViewHatenaBookmarkList.also {
            val divider = DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
            it.setHasFixedSize(true)
            it.addItemDecoration(divider)
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = bookmarkListAdapter
        }

        viewModel.bookmarkListLiveData.observe(viewLifecycleOwner, Observer {
            bookmarkListAdapter.setItemList(it)
        })

        viewModel.fetchBookmarkList()
    }
}
