package io.github.umatoma.multiwebmediaviewer.view.home

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry
import kotlinx.android.synthetic.main.adapter_hatena_entry_list_item.view.*

class HatenaEntryListAdapter() : GroupAdapter<GroupieViewHolder>() {

    class EntryItem(val entry: HatenaEntry) : Item<GroupieViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.adapter_hatena_entry_list_item
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.also {
                it.txtHatenaEntryTitle.text = entry.title
                it.txtHatenaEntryCount.text = entry.count.toString()
                it.txtHatenaEntryRootUrl.text = entry.getUrlHost()

                Picasso.get()
                    .load(entry.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(it.imgHatenaEntryImage)
            }
        }
    }

    class FooterItem() : Item<GroupieViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.adapter_hatena_entry_list_footer_item
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {}
    }

    private val footerItem = FooterItem()
    private val entryItemListSection = Section()
    private var onClickEntryListener: ((HatenaEntry) -> Unit)? = null
    private var onClickFooterListener: (() -> Unit)? = null

    init {
        add(entryItemListSection)
        setOnItemClickListener { item, view ->
            when (item) {
                is EntryItem -> onClickEntryListener?.invoke(item.entry)
                is FooterItem -> onClickFooterListener?.invoke()
            }
        }
    }

    fun onClickEntry(onClick: (HatenaEntry) -> Unit) {
        onClickEntryListener = onClick
    }

    fun onClickFooter(onClick: () -> Unit) {
        onClickFooterListener = onClick
    }

    fun setEntryList(entryList: List<HatenaEntry>) {
        entryItemListSection.clear()
        entryItemListSection.addAll(entryList.map { EntryItem(it) })

        if (entryList.isNotEmpty()) {
            entryItemListSection.setFooter(footerItem)
        }

        notifyDataSetChanged()
    }
}