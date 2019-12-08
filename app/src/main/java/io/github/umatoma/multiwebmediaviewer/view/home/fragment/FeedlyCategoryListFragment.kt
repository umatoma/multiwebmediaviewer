package io.github.umatoma.multiwebmediaviewer.view.home.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.view.home.viewModel.FeedlyCategoryListViewModel
import io.github.umatoma.multiwebmediaviewer.view.home.viewModel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_feedly_category_list.*


class FeedlyCategoryListFragment : Fragment() {

    companion object {

        fun newInstance(): FeedlyCategoryListFragment {
            return FeedlyCategoryListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedly_category_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = FeedlyCategoryListViewModel
            .Factory(requireContext())
            .create(this)
        val homeViewModel = HomeViewModel
            .Factory(requireContext())
            .create(requireActivity())

        val categoryListAdapter = FeedlyCategoryListAdapter()
            .also {
            it.onClickCategory { category ->
                homeViewModel.setFeedlyEntryListCategory(category)
            }
        }

        recyclerViewFeedlyCategoryList.also {
            val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            it.setHasFixedSize(true)
            it.addItemDecoration(divider)
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = categoryListAdapter
        }

        swipeRefreshFeedlyCategoryList.setOnRefreshListener {
            viewModel.fetchFeedlyCategoryList()
        }

        viewModel.isFetchingLiveData.observe(viewLifecycleOwner, Observer {
            swipeRefreshFeedlyCategoryList.isRefreshing = it
        })

        viewModel.feedlyCategoryListLiveData.observe(viewLifecycleOwner, Observer {
            categoryListAdapter.setCategoryList(it)
        })

        viewModel.fetchFeedlyCategoryList()
    }
}
