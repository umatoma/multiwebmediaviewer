package io.github.umatoma.multiwebmediaviewer.view.hatenaEntry

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaBookmark
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.adapter_hatena_bookmark_list_item.view.*

class HatenaBookmarkListAdapter(
    private val context: Context
): RecyclerView.Adapter<HatenaBookmarkListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val bookmarkList: MutableList<HatenaBookmark> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_hatena_bookmark_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookmark = bookmarkList.get(position)

        holder.itemView.also {
            it.txtHatenaBookmarkUser.text = bookmark.user
            it.txtHatenaBookmarkTimestamp.text = bookmark.timestamp
            it.txtHatenaBookmarkComment.text = bookmark.comment

            Picasso.get()
                .load(bookmark.getUserImageUrl())
                .fit()
                .centerCrop()
                .transform(CropCircleTransformation())
                .into(it.imgHatenaBookmarkUser)
        }
    }

    override fun getItemCount(): Int {
        return bookmarkList.size
    }

    fun setItemList(itemList: List<HatenaBookmark>) {
        bookmarkList.clear()
        bookmarkList.addAll(itemList)
        notifyDataSetChanged()
    }
}