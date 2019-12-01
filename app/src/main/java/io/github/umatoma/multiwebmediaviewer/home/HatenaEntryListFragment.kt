package io.github.umatoma.multiwebmediaviewer.home

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.hatenaAuth.HatenaAuthActivity
import io.github.umatoma.multiwebmediaviewer.hatenaEntry.HatenaEntryActivity
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

        viewModel.isSignedInAndHasChangedLiveData.observe(
            viewLifecycleOwner,
            Observer { (isSignedIn, hasChanged) ->
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

//    private fun launchCustomTab(entry: HatenaEntry) {
//        val intent =
//            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.yahoo.co.jp/"))
//        val actionIntent =
//            PendingIntent.getActivity(requireContext(), 0, intent, 0)
//        val toolbarColor =
//            ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
//
//        // NOTE: setSecondaryToolbarViews() does not work...
//        //       So, I use addToolbarItem() though it is deprecated.
//        val customTabsIntent = CustomTabsIntent.Builder()
//            .setToolbarColor(toolbarColor)
//            .setSecondaryToolbarColor(toolbarColor)
//            .addToolbarItem(1, getBitmap(R.drawable.ic_comment), "Comment", actionIntent)
//            .addToolbarItem(2, getBitmap(R.drawable.ic_hatena_logo), "Bookmark", actionIntent)
//            .addToolbarItem(3, getBitmap(R.drawable.ic_share), "Share", actionIntent)
//            .addDefaultShareMenuItem()
//            .setShowTitle(true)
//            .build()
//
//        customTabsIntent.launchUrl(requireContext(), Uri.parse(entry.url))
//    }

    private fun getBitmap(drawableId: Int): Bitmap {
        return ResourcesCompat.getDrawable(resources, drawableId, null)!!.toBitmap()
    }
}
