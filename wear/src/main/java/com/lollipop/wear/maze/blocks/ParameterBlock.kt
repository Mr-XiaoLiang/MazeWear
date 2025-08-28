package com.lollipop.wear.maze.blocks

import android.graphics.Color
import androidx.annotation.DrawableRes
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.heightWrap
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.dsl.widthMatch
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.SP
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.Row
import com.lollipop.wear.blocksbuilding.view.Text
import com.lollipop.wear.blocksbuilding.view.TextStyle

fun BuilderScope.ParameterItem(@DrawableRes icon: Int, label: DataProvider<String>) {
    ItemView {
        content.layoutParams(ItemSize.Match, ItemSize.Wrap)
        Row(
            layoutParams = ViewLayoutParams().widthMatch().heightWrap()
        ) {

            Text(
                layoutParams = ViewLayoutParams(width = ItemSize.Match, height = ItemSize.Wrap)
                    .weight(1F)
                    .gravity(ViewGravity.CenterVertical)
            ) {
                gravity(ViewGravity.CenterVertical)
                fontSize(14.SP)
                padding(horizontal = 12.DP, vertical = 6.DP)
                color(Color.WHITE)
                label.remember {
                    text = it
                }
            }
        }
    }
}