/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.lollipop.wear.maze.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lollipop.play.core.data.DataManager
import com.lollipop.wear.maze.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(R.style.MazeTheme_Play)
        setContentView(binding.root)
        initView()
        select(Subpage.Home, false)
    }

    override fun onResume() {
        super.onResume()
        DataManager.load(this) {
            // TODO 也不需要做什么吧
        }
    }

    private fun initView() {
        binding.viewPager.adapter = adapter
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

}
