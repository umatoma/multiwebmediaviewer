package io.github.umatoma.multiwebmediaviewer.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.common.hatena.entity.HatenaEntry
import kotlinx.android.synthetic.main.adapter_hatena_entry_list_item.view.*
import java.net.URL

class HatenaEntryListAdapter(
    private val onClickItem: (item: HatenaEntry) -> Unit
): RecyclerView.Adapter<HatenaEntryListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val entryList: MutableList<HatenaEntry> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_hatena_entry_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entryList.get(position)

        holder.itemView.also {
            it.setOnClickListener { onClickItem(entry) }
            it.txtHatenaEntryTitle.text = entry.title
            it.txtHatenaEntryRootUrl.text = URL(entry.rootUrl).host

            Picasso.get()
                .load(entry.imageUrl)
                .fit()
                .centerCrop()
                .into(it.imgHatenaEntryImage)
        }
    }

    override fun getItemCount(): Int {
        return entryList.size
    }

    fun setItemList(itemList: List<HatenaEntry>) {
        entryList.clear()
        entryList.addAll(itemList)
        notifyDataSetChanged()
    }
}