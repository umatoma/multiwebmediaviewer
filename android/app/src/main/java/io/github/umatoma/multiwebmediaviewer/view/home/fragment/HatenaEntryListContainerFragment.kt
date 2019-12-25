package io.github.umatoma.multiwebmediaviewer.view.home.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.view.hatenaAuth.HatenaAuthActivity
import io.github.umatoma.multiwebmediaviewer.view.home.viewModel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_hatena_entry_list_container.*

class HatenaEntryListContainerFragment : Fragment() {

    fun getTitle(context: Context): String {
        return context.getString(R.string.fragment_hatena_entry_list_title)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hatena_entry_list_container, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val homeViewModelFactory = HomeViewModel.Factory(requireContext())
        val homeViewModel = ViewModelProviders
            .of(requireActivity(), homeViewModelFactory)
            .get(HomeViewModel::class.java)

        viewPagerHatenaEntryList.adapter =
            HatenaEntryListPagerAdapter(
                requireFragmentManager()
            )

        btnHatenaSignIn.setOnClickListener {
            startActivity(Intent(requireContext(), HatenaAuthActivity::class.java))
        }

        homeViewModel.isSignedInHatenaLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                layoutHatenaEntryListContent.visibility = View.VISIBLE
                layoutHatenaSignInContent.visibility = View.GONE
            } else {
                layoutHatenaEntryListContent.visibility = View.GONE
                layoutHatenaSignInContent.visibility = View.VISIBLE
            }
        })
    }
}
