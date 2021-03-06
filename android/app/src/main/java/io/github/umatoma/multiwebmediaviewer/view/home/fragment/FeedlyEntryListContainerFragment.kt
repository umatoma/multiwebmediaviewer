package io.github.umatoma.multiwebmediaviewer.view.home.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.view.feedlyAuth.FeedlyAuthActivity
import io.github.umatoma.multiwebmediaviewer.view.home.viewModel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_feedly_entry_list_container.*


class FeedlyEntryListContainerFragment : Fragment() {

    fun getTitle(context: Context): String {
        return context.getString(R.string.fragment_feedly_entry_list_title)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedly_entry_list_container, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val homeViewModel = HomeViewModel.Factory(requireContext()).create(requireActivity())
        val pagerAdapter =
            FeedlyEntryListPagerAdapter(
                requireFragmentManager()
            )

        viewPagerFeedlyEntryList.adapter = pagerAdapter

        btnFeedlySignIn.setOnClickListener {
            FeedlyAuthActivity.startActivity(requireContext())
        }

        homeViewModel.isSignedInFeedlyLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                layoutFeedlyEntryListContent.visibility = View.VISIBLE
                layoutFeedlySignInContent.visibility = View.GONE
            } else {
                layoutFeedlyEntryListContent.visibility = View.GONE
                layoutFeedlySignInContent.visibility = View.VISIBLE
            }
        })

        homeViewModel.feedlyEntryListCategoryLiveData.observe(viewLifecycleOwner, Observer {
            viewPagerFeedlyEntryList.currentItem =
                FeedlyEntryListPagerAdapter.POSITION_FEEDS
        })
    }
}
