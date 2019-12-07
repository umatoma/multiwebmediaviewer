package io.github.umatoma.multiwebmediaviewer.view.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry

class FeedlyEntryListPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        const val POSITION_FEEDS = 0;
        const val POSITION_COLLECTIONS = 1;
    }

    private val categoryList: List<HatenaEntry.Category> = listOf()

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            POSITION_FEEDS -> "Feeds"
            POSITION_COLLECTIONS -> "Collections"
            else -> throw Exception("Invalid page position")
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            POSITION_FEEDS -> FeedlyEntryListFragment.newInstance()
            POSITION_COLLECTIONS -> FeedlyCategoryListFragment.newInstance()
            else -> throw Exception("Invalid page position")
        }
    }

    override fun getCount(): Int {
        return 2
    }

}