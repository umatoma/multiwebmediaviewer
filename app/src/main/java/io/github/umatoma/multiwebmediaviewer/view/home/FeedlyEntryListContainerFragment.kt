package io.github.umatoma.multiwebmediaviewer.view.home


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.view.feedlyAuth.FeedlyAuthActivity
import io.github.umatoma.multiwebmediaviewer.viewModel.home.HomeViewModel
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

        val homeViewModelFactory = HomeViewModel.Factory(requireContext())
        val homeViewModel = ViewModelProviders
            .of(requireActivity(), homeViewModelFactory)
            .get(HomeViewModel::class.java)

        btnFeedlySignIn.setOnClickListener {
            FeedlyAuthActivity.startActivity(requireContext())
        }

        homeViewModel.isSignedInFeedlyLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                btnFeedlySignIn.visibility = View.GONE
            } else {
                btnFeedlySignIn.visibility = View.VISIBLE
            }
        })
    }
}
