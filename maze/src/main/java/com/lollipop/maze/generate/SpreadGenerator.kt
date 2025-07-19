package com.lollipop.maze.generate

import com.lollipop.maze.data.MMap
import java.util.LinkedList
import kotlin.random.Random

object SpreadGenerator : BasicAverageGenerator() {

    override fun buildByBlueprint(blueprint: MMap) {

        val startBlock = findBuildStart(blueprint)

        val pending = LinkedList<PendingDirection>()

        blueprint.setRoad(startBlock)
        // 添加起点，先把四个方向加进去
        val startLeft = startBlock.left(1)
        val startRight = startBlock.right(1)
        val startTop = startBlock.up(1)
        val startBottom = startBlock.down(1)
        pending.add(PendingDirection(startLeft, startLeft.left(1)))
        pending.add(PendingDirection(startRight, startRight.right(1)))
        pending.add(PendingDirection(startTop, startTop.up(1)))
        pending.add(PendingDirection(startBottom, startBottom.down(1)))

        while (pending.isNotEmpty()) {
            // 通过随机数来获取一个待处理的方向，通过方向的先后关系来模拟随机的道路弯曲
            val randomIndex = Random.nextInt(pending.size)
            val directionInfo = pending.removeAt(randomIndex)

            val pendingBlock = directionInfo.pendingBlock
            val wallBlock = directionInfo.wallBlock

            if (!blueprint.isRoadPending(pendingBlock)
                || !blueprint.isWallPending(wallBlock)
            ) {
                if (blueprint.isWallPending(wallBlock)) {
                    blueprint.setWall(wallBlock)
                }
            } else {
                blueprint.setRoad(pendingBlock)
                blueprint.setRoad(wallBlock)

                val left = pendingBlock.left(1)
                if (blueprint.isWallPending(left)) {
                    pending.add(PendingDirection(left, left.left(1)))
                }

                val right = pendingBlock.right(1)
                if (blueprint.isWallPending(right)) {
                    pending.add(PendingDirection(right, right.right(1)))
                }

                val up = pendingBlock.up(1)
                if (blueprint.isWallPending(up)) {
                    pending.add(PendingDirection(up, up.up(1)))
                }

                val down = pendingBlock.down(1)
                if (blueprint.isWallPending(down)) {
                    pending.add(PendingDirection(down, down.down(1)))
                }
            }
        }
    }

    private fun MMap.setRoad(block: Block) {
        set(block.x, block.y, ROAD)
    }

    private fun MMap.setWall(block: Block) {
        set(block.x, block.y, WALL)
    }

    /**
     * 封死
     */
    private fun sealed(blueprint: MMap, block: Block) {
        val type = blueprint[block.x, block.y]
        if (type == WALL_PENDING) {
            blueprint.set(x = block.x, y = block.y, value = WALL)
        }
    }

    private fun MMap.spreadEnable(block: Block, spreadX: Int, spreadY: Int): Boolean {
        val blueprint = this
        if (!blueprint.isWallPending(block)) {
            return false
        }
        return blueprint.isRoadPending(block.x + spreadX, block.y + spreadY)
    }

    private fun collectWallPending(role: Block, blueprint: MMap, out: MutableList<Block>) {
        if (blueprint.isWallPending(role.x, role.y - 1)) {
            out.add(Block(role.x, role.y - 1))
        }
        if (blueprint.isWallPending(role.x, role.y + 1)) {
            out.add(Block(role.x, role.y + 1))
        }
        if (blueprint.isWallPending(role.x - 1, role.y)) {
            out.add(Block(role.x - 1, role.y))
        }
        if (blueprint.isWallPending(role.x + 1, role.y)) {
            out.add(Block(role.x + 1, role.y))
        }
    }

    private class PendingDirection(
        val wallBlock: Block,
        val pendingBlock: Block,
    )

}