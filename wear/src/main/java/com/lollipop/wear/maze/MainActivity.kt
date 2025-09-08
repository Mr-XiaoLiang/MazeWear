package com.lollipop.wear.maze

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.helper.registerLog
import com.lollipop.wear.maze.databinding.ActivityMainBinding
import com.lollipop.wear.maze.main.ConfigFragment
import com.lollipop.wear.maze.main.HistoryFragment
import com.lollipop.wear.maze.main.HomeFragment
import com.lollipop.wear.maze.main.MainBaseFragment

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val adapter by lazy {
        SubpageAdapter(this)
    }

    private val log = registerLog()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(R.style.MazeTheme_Default)
        setContentView(binding.root)
        initView()
        select(Subpage.Home, false)
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
        DataManager.load(this) {
            log("DataManager load end")
        }
    }

    private fun initView() {
        binding.viewPager.adapter = adapter
        binding.viewPager.setPageTransformer(ScaledPageTransformer())
        binding.pageIndicator.bind(binding.viewPager)
    }

    private fun select(page: Subpage, smoothScroll: Boolean = true) {
        binding.viewPager.setCurrentItem(page.ordinal, smoothScroll)
    }

    private class SubpageAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {


        override fun getItemCount(): Int {
            return Subpage.entries.size
        }

        override fun createFragment(position: Int): Fragment {
            return Subpage.entries[position].clazz.getDeclaredConstructor().newInstance()
        }
    }

    private enum class Subpage(
        val clazz: Class<out MainBaseFragment>
    ) {
        History(HistoryFragment::class.java),
        Home(HomeFragment::class.java),
        Config(ConfigFragment::class.java),
    }

    private class ScaledPageTransformer(
        val minScale: Float = 0.8F,
        val minAlpha: Float = 1F
    ) : ViewPager2.PageTransformer {

        override fun transformPage(page: View, position: Float) {
            page.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }

                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well.
                        val scaleFactor = Math.max(minScale, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1).
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha =
                            (minAlpha + (((scaleFactor - minScale) / (1 - minScale)) * (1 - minAlpha)))
                    }

                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }

    }

}
