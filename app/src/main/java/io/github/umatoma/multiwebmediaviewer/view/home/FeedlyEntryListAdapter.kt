package io.github.umatoma.multiwebmediaviewer.view.home

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCategory
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyEntry
import kotlinx.android.synthetic.main.adapter_feedly_entry_list_header_item.view.*
import kotlinx.android.synthetic.main.adapter_feedly_entry_list_item.view.*

class FeedlyEntryListAdapter() : GroupAdapter<GroupieViewHolder>() {

    class HeaderItem(val category: FeedlyCategory) : Item<GroupieViewHolder>() {

        override fun getLayout(): Int = R.layout.adapter_feedly_entry_list_header_item

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.also {
                it.txtFeedlyEntryCategory.text = category.label
            }
        }
    }

    class EntryItem(val entry: FeedlyEntry) : Item<GroupieViewHolder>() {

        override fun getLayout(): Int = R.layout.adapter_feedly_entry_list_item

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.also {
                it.txtFeedlyEntryTitle.text = entry.title
                it.txtFeedlyEntryEngagement.text = (entry.engagement ?: 0).toString()
                it.txtFeedlyEntryRootUrl.text = entry.origin.title

                Picasso.get()
                    .load(entry.visual?.url)
                    .fit()
                    .centerCrop()
                    .into(it.imgFeedlyEntryImage)
            }
        }
    }

    class FooterItem() : Item<GroupieViewHolder>() {

        override fun getLayout(): Int = R.layout.adapter_feedly_entry_list_footer_item

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {}
    }

    private val footerItem = FooterItem()
    private val entryItemListSection = Section()
    private var onClickEntryListener: ((FeedlyEntry) -> Unit)? = null
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

    fun onClickEntry(onClick: (FeedlyEntry) -> Unit) {
        onClickEntryListener = onClick
    }

    fun onClickFooter(onClick: () -> Unit) {
        onClickFooterListener = onClick
    }

    fun setEntryList(entryList: List<FeedlyEntry>) {
        entryItemListSection.clear()
        entryItemListSection.addAll(entryList.map { EntryItem(it) })

        if (entryList.isNotEmpty()) {
            entryItemListSection.setFooter(footerItem)
        }

        notifyDataSetChanged()
    }

    fun setCategory(category: FeedlyCategory) {
        entryItemListSection.setHeader(HeaderItem(category))
    }
}