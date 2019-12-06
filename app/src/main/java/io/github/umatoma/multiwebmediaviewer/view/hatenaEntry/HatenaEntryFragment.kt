package io.github.umatoma.multiwebmediaviewer.view.hatenaEntry

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry
import kotlinx.android.synthetic.main.fragment_hatena_entry.*


class HatenaEntryFragment : Fragment() {

    companion object {

        private const val KEY_ENTRY= "KEY_ENTRY"

        fun newInstance(entry: HatenaEntry): HatenaEntryFragment {
            val bundle = Bundle().also {
                it.putSerializable(KEY_ENTRY, entry)
            }
            return HatenaEntryFragment().also {
                it.arguments = bundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hatena_entry, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val entry = requireArguments().getSerializable(KEY_ENTRY) as HatenaEntry

        webViewHatenaEntry.let {
            it.webViewClient = object : WebViewClient() {}
            it.settings.javaScriptEnabled = true
            it.loadUrl(entry.url)
        }
    }
}
