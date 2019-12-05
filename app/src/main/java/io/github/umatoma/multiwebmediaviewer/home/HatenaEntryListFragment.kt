package io.github.umatoma.multiwebmediaviewer.home


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
import io.github.umatoma.multiwebmediaviewer.common.hatena.entity.HatenaEntry
import io.github.umatoma.multiwebmediaviewer.hatenaEntry.HatenaEntryActivity
import kotlinx.android.synthetic.main.fragment_hatena_entry_list.*

class HatenaEntryListFragment : Fragment() {

    companion object {
        private const val KEY_ENTRY_TYPE = "KEY_ENTRY_TYPE"
        private const val KEY_ENTRY_CATEGORY = "KEY_ENTRY_CATEGORY"

        fun newInstance(
            kind: HatenaEntry.Kind,
            category: HatenaEntry.Category
        ): HatenaEntryListFragment {
            val bundle = Bundle().also {
                it.putSerializable(KEY_ENTRY_TYPE, kind)
                it.putSerializable(KEY_ENTRY_CATEGORY, category)
            }
            return HatenaEntryListFragment().also {
                it.arguments = bundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hatena_entry_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val entryType = requireArguments()
            .getSerializable(KEY_ENTRY_TYPE) as HatenaEntry.Kind
        val entryCategory = requireArguments()
            .getSerializable(KEY_ENTRY_CATEGORY) as HatenaEntry.Category

        val viewModelFactory =
            HatenaEntryListViewModel.Factory(entryType, entryCategory, requireContext())
        val viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(HatenaEntryListViewModel::class.java)

        val entryListAdapter = HatenaEntryListAdapter().also {
            it.onClickEntry { entry ->
                HatenaEntryActivity.startActivity(requireContext(), entry)
            }
            it.onClickFooter {
                viewModel.fetchHatenaEntryListOnNextPage()
            }
        }

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

        swipeRefreshHatenaEntryList.setOnRefreshListener {
            viewModel.fetchHatenaEntryList()
        }

        viewModel.isFetchingLiveData.observe(viewLifecycleOwner, Observer {
            swipeRefreshHatenaEntryList.isRefreshing = it
        })

        viewModel.hatenaEntryListLiveData.observe(viewLifecycleOwner, Observer {
            entryListAdapter.setEntryList(it)
        })

        viewModel.fetchHatenaEntryList()
    }
}
