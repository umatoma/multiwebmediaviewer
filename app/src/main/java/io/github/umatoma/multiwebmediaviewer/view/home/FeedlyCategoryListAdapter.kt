package io.github.umatoma.multiwebmediaviewer.view.home

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCategory
import kotlinx.android.synthetic.main.adapter_feedly_category_list_item.view.*

class FeedlyCategoryListAdapter() : GroupAdapter<GroupieViewHolder>() {

    class CategoryItem(val category: FeedlyCategory) : Item<GroupieViewHolder>() {

        override fun getLayout(): Int = R.layout.adapter_feedly_category_list_item

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.also {
                it.txtFeedlyCategoryLabel.text = category.label
            }
        }
    }

    private val categoryItemListSection = Section()
    private var onClickCategoryListener: ((FeedlyCategory) -> Unit)? = null

    init {
        add(categoryItemListSection)
        setOnItemClickListener { item, view ->
            when (item) {
                is CategoryItem -> onClickCategoryListener?.invoke(item.category)
            }
        }
    }

    fun onClickCategory(onClick: (FeedlyCategory) -> Unit) {
        onClickCategoryListener = onClick
    }

    fun setCategoryList(categoryList: List<FeedlyCategory>) {
        categoryItemListSection.clear()
        categoryItemListSection.addAll(categoryList.map { CategoryItem(it) })

        notifyDataSetChanged()
    }
}