package io.github.umatoma.multiwebmediaviewer.view.home.fragment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyEntry
import io.github.umatoma.multiwebmediaviewer.view.home.viewModel.FeedlyEntryListViewModel
import io.github.umatoma.multiwebmediaviewer.view.home.viewModel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_feedly_entry_list.*


class FeedlyEntryListFragment : Fragment() {

    companion object {

        fun newInstance(): FeedlyEntryListFragment {
            return FeedlyEntryListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedly_entry_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = FeedlyEntryListViewModel.Factory(requireContext()).create(this)
        val homeViewModel = HomeViewModel.Factory(requireContext()).create(requireActivity())

        val entryListAdapter = FeedlyEntryListAdapter()
            .also {
                it.onClickEntry { entry -> displayEntryView(entry) }
                it.onClickFooter { viewModel.fetchFeedlyEntryListOnNextPage() }
                it.onLongClickEntry { entry -> shareEntry(entry) }
            }

        recyclerViewFeedlyEntryList.also {
            val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            it.setHasFixedSize(true)
            it.addItemDecoration(divider)
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = entryListAdapter
        }

        swipeRefreshFeedlyEntryList.setOnRefreshListener {
            viewModel.fetchFeedlyEntryList()
        }

        viewModel.isFetchingLiveData.observe(viewLifecycleOwner, Observer {
            swipeRefreshFeedlyEntryList.isRefreshing = it
        })

        viewModel.feedlyCategoryLiveData.observe(viewLifecycleOwner, Observer {
            entryListAdapter.setCategory(it)
        })

        viewModel.feedlyEntryListLiveData.observe(viewLifecycleOwner, Observer {
            entryListAdapter.setEntryList(it)
        })

        homeViewModel.feedlyEntryListCategoryLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.setCategory(it)
        })

        viewModel.fetchFeedlyEntryList()
    }

    private fun displayEntryView(entry: FeedlyEntry) {
        entry.getEntryUrl().also { url ->
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
        }
    }

    private fun shareEntry(entry: FeedlyEntry) {
        val sendIntent = Intent().also {
            it.action = Intent.ACTION_SEND
            it.putExtra(Intent.EXTRA_TEXT, entry.getEntryUrl())
            it.type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}
