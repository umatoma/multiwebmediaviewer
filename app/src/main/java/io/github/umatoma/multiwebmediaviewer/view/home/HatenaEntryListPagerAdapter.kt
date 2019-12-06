package io.github.umatoma.multiwebmediaviewer.view.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry

class HatenaEntryListPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val categoryList: List<HatenaEntry.Category> = listOf()

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "新着"
            1 -> "人気エントリー"
            else -> throw Exception("Invalid page position")
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HatenaEntryListFragment.newInstance(HatenaEntry.Kind.NEW, HatenaEntry.Category.ALL)
            1 -> HatenaEntryListFragment.newInstance(HatenaEntry.Kind.HOT, HatenaEntry.Category.ALL)
            else -> throw Exception("Invalid page position")
        }
    }

    override fun getCount(): Int {
        return 2
    }

}