package com.magericx.storagemanipulator

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference

class ViewPagerListener(bnv: WeakReference<BottomNavigationView>) :
    ViewPager2.OnPageChangeCallback() {
    private val bottomNavigationView = bnv.get()
    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        when (position) {
            0 -> bottomNavigationView?.menu?.findItem(R.id.navigation_dashboard)?.isChecked =
                true
            1 -> bottomNavigationView?.menu?.findItem(R.id.internal_storage)?.isChecked =
                true
            2 -> bottomNavigationView?.menu?.findItem(R.id.external_storage)?.isChecked =
                true
        }
    }
}

