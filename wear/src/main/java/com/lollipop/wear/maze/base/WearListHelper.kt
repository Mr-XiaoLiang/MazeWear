package com.lollipop.wear.maze.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lollipop.wear.maze.databinding.ItemMainFooterBinding
import com.lollipop.wear.maze.databinding.ItemMainTitleBinding

class WearListHelper {

    val titleAdapter by lazy {
        TitleAdapter()
    }

    val footerAdapter by lazy {
        FooterAdapter()
    }

    fun updateTitle(title: String) {
        titleAdapter.updateTitle(title)
    }

    fun updateTime(time: String) {
        titleAdapter.updateTime(time)
    }

    fun createAdapter(vararg contentAdapter: RecyclerView.Adapter<*>): ConcatAdapter {
        return ConcatAdapter(titleAdapter, *contentAdapter, footerAdapter)
    }

    abstract class BasicAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

        private var layoutInflaterRef: LayoutInflater? = null

        protected fun layoutInflater(parent: ViewGroup): LayoutInflater {
            val ref = layoutInflaterRef
            if (ref != null) {
                return ref
            }
            val layoutInflater = LayoutInflater.from(parent.context)
            layoutInflaterRef = layoutInflater
            return layoutInflater
        }

        protected inline fun <reified T : ViewBinding> inflate(parent: ViewGroup): T {
            return T::class.java.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            ).invoke(null, layoutInflater(parent), parent, false) as T
        }

    }

    class TitleAdapter(
        private var titleValue: String = "",
        private var timeValue: String = "",
    ) : BasicAdapter<TitleHolder>() {

        private var titleHolder: TitleHolder? = null

        fun updateTitle(title: String) {
            titleValue = title
            notifyItemChanged(0)
        }

        fun updateTime(time: String) {
            timeValue = time
            titleHolder?.updateTime(time)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TitleHolder {
            val holder = TitleHolder(inflate(parent))
            titleHolder = holder
            return holder
        }

        override fun onBindViewHolder(
            holder: TitleHolder,
            position: Int
        ) {
            holder.bind(titleValue, timeValue)
        }

        override fun getItemCount(): Int {
            return 1
        }
    }

    class FooterAdapter(
    ) : BasicAdapter<FooterHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FooterHolder {
            return FooterHolder(inflate(parent))
        }

        override fun onBindViewHolder(
            holder: FooterHolder,
            position: Int
        ) {
        }

        override fun getItemCount(): Int {
            return 1
        }
    }

    class TitleHolder(
        private val binding: ItemMainTitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, time: String) {
            binding.titleView.text = title
            updateTime(time)
        }

        fun updateTime(time: String) {
            binding.timeView.text = time
        }

    }

    class FooterHolder(
        private val binding: ItemMainFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }

}