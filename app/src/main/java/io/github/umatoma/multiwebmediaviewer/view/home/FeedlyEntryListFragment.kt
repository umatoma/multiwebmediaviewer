package io.github.umatoma.multiwebmediaviewer.view.home


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.viewModel.home.FeedlyEntryListViewModel
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

        val viewModelFactory = FeedlyEntryListViewModel.Factory(requireContext())
        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FeedlyEntryListViewModel::class.java)

        val entryListAdapter = FeedlyEntryListAdapter().also {
            it.onClickEntry { entry ->
                val customTabsIntent = CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(requireContext(), Uri.parse(entry.canonicalUrl))
            }
            it.onClickFooter {
                viewModel.fetchFeedlyEntryListOnNextPage()
            }
        }

        recyclerViewFeedlyEntryList.also {
            val divider = DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
            it.setHasFixedSize(true)
            it.addItemDecoration(divider)
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = entryListAdapter
        }

        viewModel.feedlyCategoryLiveData.observe(viewLifecycleOwner, Observer {
            entryListAdapter.setCategory(it)
        })

        viewModel.feedlyEntryListLiveData.observe(viewLifecycleOwner, Observer {
            entryListAdapter.setEntryList(it)
        })

        viewModel.fetchFeedlyEntryList()
    }
}
