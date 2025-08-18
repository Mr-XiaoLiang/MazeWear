package com.lollipop.wear.maze.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.play.core.kit.MazeSizeActivity
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.base.WearListHelper
import com.lollipop.wear.maze.databinding.FragmentMainSubpageBinding
import com.lollipop.wear.maze.databinding.ItemMainConfigBinding

class ConfigFragment : MainBaseFragment() {

    private val configAdapter by lazy {
        ConfigAdapter(::getSummary, ::onItemClick)
    }

    private var appVersion = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appVersion = context.packageManager.getPackageInfo(
            context.packageName,
            0
        ).versionName ?: ""
    }

    override fun onViewCreated(binding: FragmentMainSubpageBinding) {
        updateTitle(binding.root.context.getString(R.string.title_config))
        initRecyclerView(binding, configAdapter)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        configAdapter.notifyDataSetChanged()
    }

    private fun getSummary(item: ConfigItem): String {
        when (item) {
            ConfigItem.MapSize -> {
                return settings?.mazeWidth?.let { "${it}x${it}" } ?: ""
            }

            ConfigItem.Theme -> {
                return "NONE"
            }

            ConfigItem.About -> {
                return appVersion
            }
        }
    }

    private fun onItemClick(item: ConfigItem) {
        when (item) {
            ConfigItem.MapSize -> {
                activity?.let {
                    it.startActivity(Intent(it, MazeSizeActivity::class.java))
                }
            }

            ConfigItem.Theme -> {
                // TODO
            }

            ConfigItem.About -> {
                // TODO
            }
        }

    }

    private class ConfigAdapter(
        private val summaryProvider: (ConfigItem) -> String,
        private val onItemClick: (ConfigItem) -> Unit
    ) : WearListHelper.BasicAdapter<ConfigHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ConfigHolder {
            return ConfigHolder(inflate(parent), ::onItemClick)
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= ConfigItem.entries.size) {
                return
            }
            val item = ConfigItem.entries[position]
            onItemClick(item)
        }

        override fun onBindViewHolder(
            holder: ConfigHolder,
            position: Int
        ) {
            val item = ConfigItem.entries[position]
            val summary = summaryProvider(item)
            holder.bind(item.titleId, summary, item.hasArrow)
        }

        override fun getItemCount(): Int {
            return ConfigItem.entries.size
        }
    }

    private class ConfigHolder(
        private val binding: ItemMainConfigBinding,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick()
            }
        }

        private fun onItemClick() {
            onClick(bindingAdapterPosition)
        }

        fun bind(titleId: Int, summary: String, hasArrow: Boolean) {
            binding.titleView.setText(titleId)
            binding.summaryView.text = summary
            binding.summaryView.isVisible = summary.isNotEmpty()
            binding.arrowIcon.isVisible = hasArrow
        }

    }

    private enum class ConfigItem(
        val titleId: Int,
        val hasArrow: Boolean
    ) {
        MapSize(titleId = R.string.label_map_size, hasArrow = true),
        Theme(titleId = R.string.label_theme, hasArrow = true),
        About(titleId = R.string.label_about, hasArrow = true)
    }

}