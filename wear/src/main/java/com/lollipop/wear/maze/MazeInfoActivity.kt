package com.lollipop.wear.maze

import android.content.Context
import android.os.Bundle
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.wear.blocksbuilding.dsl.blockStateOf
import com.lollipop.wear.maze.base.MazeActivityHelper
import com.lollipop.wear.maze.base.WearBlocksActivity
import com.lollipop.wear.maze.blocks.MazeOverviewBlockState
import com.lollipop.wear.maze.blocks.titleHeader


//class MazeInfoActivity : WearComponentActivity() {
//
//    companion object {
//        fun start(context: Context, mazeCache: String) {
//            MazeActivityHelper.startWithMaze<MazeInfoActivity>(context, mazeCache)
//        }
//    }
//
//    private val mazeNameState = mutableStateOf("")
//    private val mazeSizeState = mutableStateOf("")
//    private val mazeTimeState = mutableStateOf("")
//    private val mazeStepsState = mutableStateOf("")
//
//    private var mazeCache: String = ""
//
//    private val mazeOverviewState = MazeOverviewState()
//
//    private val deleteController = ProgressButtonController(
//        delay = 2000L,
//        onTimeEnd = ::onDeleteTimeEnd
//    ).bind(this)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        initData()
//    }
//
//    private fun initData() {
//        val cache = MazeActivityHelper.findFromIntent(intent)
//        mazeCache = cache
//        DataManager.findByFile(cache)?.let {
//            onMazeLoaded(it)
//        }
//    }
//
//    private fun onMazeLoaded(mazeInfo: MazeHistory) {
//        mazeNameState.value = mazeInfo.name.ifEmpty { "???" }
//        mazeSizeState.value = mazeInfo.level
//        mazeTimeState.value = mazeInfo.timeDisplay
//        mazeStepsState.value = mazeInfo.pathLength.toString()
//        mazeOverviewState.update(mazeInfo.maze)
//        mazeOverviewState.update(mazeInfo.path)
//    }
//
//    private fun onDeleteTimeEnd() {
//        DataManager.delete(mazeCache)
//        onBackPressedDispatcher.onBackPressed()
//    }
//
//    private fun onOpenClick() {
//        PlayActivity.resumeMaze(this, mazeCache)
//    }
//
//    @Composable
//    override fun Content() {
//        val mazeName by remember { mazeNameState }
//        val mazeSize by remember { mazeSizeState }
//        val mazeSteps by remember { mazeStepsState }
//        val mazeTime by remember { mazeTimeState }
//        ListScaffold { transformationSpec ->
//            ListTitle(mazeName, transformationSpec)
//            MazeItem(
//                iconId = R.drawable.baseline_resize_24,
//                label = mazeSize,
//                transformationSpec = transformationSpec
//            )
//            MazeItem(
//                iconId = R.drawable.baseline_footprint_24,
//                label = mazeSteps,
//                transformationSpec = transformationSpec
//            )
//            MazeItem(
//                iconId = R.drawable.baseline_nest_clock_farsight_analog_24,
//                label = mazeTime,
//                transformationSpec = transformationSpec
//            )
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth(
//                            if (isScreenRound) {
//                                0.8F
//                            } else {
//                                0.9F
//                            }
//                        )
//                        .aspectRatio(1F)
//                        .background(color = Color.Black, shape = RoundedCornerShape(8.dp))
//                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    MazeOverview(
//                        modifier = Modifier
//                            .fillMaxSize(0.95F),
//                        state = mazeOverviewState
//                    )
//                }
//            }
//            ListSpacer(transformationSpec = transformationSpec, height = 8.dp)
//            ListButton(
//                transformationSpec = transformationSpec,
//                onClick = { onOpenClick() },
//                icon = {
//                    Icon(
//                        modifier = Modifier.size(24.dp),
//                        imageVector = Icons.Default.VideogameAsset,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onSecondary
//                    )
//                },
//                label = {
//                    Text(
//                        text = stringResource(R.string.label_open_maze),
//                        color = MaterialTheme.colorScheme.onSecondary,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            )
//            ListSpacer(transformationSpec = transformationSpec, height = 16.dp)
//            DeleteButton(transformationSpec = transformationSpec)
//        }
//    }
//
//    private fun TransformingLazyColumnScope.DeleteButton(
//        transformationSpec: TransformationSpec
//    ) {
//        ProgressButton(
//            transformationSpec = transformationSpec,
//            controller = deleteController,
//            icon = { state ->
//                when (state) {
//                    ProgressButtonState.Idle -> {
//                        Icon(
//                            imageVector = Icons.Default.Delete,
//                            modifier = Modifier.size(20.dp),
//                            tint = MaterialTheme.colorScheme.onSecondary,
//                            contentDescription = null
//                        )
//                    }
//
//                    ProgressButtonState.Pending -> {
//                        val deleteProgress by remember { deleteController.progressState }
//                        ProgressButtonPendingIndicator(progress = { deleteProgress })
//                    }
//
//                    ProgressButtonState.Done -> {
//                        Icon(
//                            imageVector = Icons.Default.Done,
//                            modifier = Modifier.size(20.dp),
//                            tint = MaterialTheme.colorScheme.onSecondary,
//                            contentDescription = null
//                        )
//                    }
//                }
//            },
//            label = { state ->
//                when (state) {
//                    ProgressButtonState.Idle -> {
//                        Text(
//                            text = stringResource(R.string.label_delete),
//                            color = MaterialTheme.colorScheme.onSecondary,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//
//                    ProgressButtonState.Pending -> {
//                        Text(
//                            text = stringResource(R.string.label_delete_cancel),
//                            color = MaterialTheme.colorScheme.onSecondary,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//
//                    ProgressButtonState.Done -> {
//                        Text(
//                            text = stringResource(R.string.label_delete_done),
//                            color = MaterialTheme.colorScheme.onSecondary,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            },
//        )
//    }
//
//    private fun TransformingLazyColumnScope.MazeItem(
//        iconId: Int,
//        label: String,
//        transformationSpec: TransformationSpec,
//    ) {
//        MazeItem(
//            icon = { painterResource(iconId) },
//            label = label,
//            transformationSpec = transformationSpec
//        )
//    }
//
//    private fun TransformingLazyColumnScope.MazeItem(
//        icon: @Composable () -> Painter,
//        label: String,
//        transformationSpec: TransformationSpec,
//    ) {
//        item {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp, vertical = 4.dp)
//                    .transformedHeight(this, transformationSpec),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Start
//            ) {
//                Icon(
//                    painter = icon(),
//                    contentDescription = null,
//                    modifier = Modifier.size(24.dp),
//                    tint = Color.White
//                )
//                Spacer(modifier = Modifier.size(8.dp))
//                Text(
//                    text = label,
//                    modifier = Modifier.weight(1f),
//                    fontSize = 14.sp,
//                    color = Color.White
//                )
//            }
//        }
//    }
//
//}

class MazeInfoActivity : WearBlocksActivity() {

    companion object {
        fun start(context: Context, mazeCache: String) {
            MazeActivityHelper.startWithMaze<MazeInfoActivity>(context, mazeCache)
        }
    }

    private val mazeNameState = blockStateOf("")
    private val mazeSizeState = blockStateOf("")
    private val mazeTimeState = blockStateOf("")
    private val mazeStepsState = blockStateOf("")
    private val mazeOverviewState = MazeOverviewBlockState()

    private var mazeCache: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        content {
            item {
                titleHeader(title = mazeNameState)
            }
        }
    }

    private fun initData() {
        val cache = MazeActivityHelper.findFromIntent(intent)
        mazeCache = cache
        DataManager.findByFile(cache)?.let {
            onMazeLoaded(it)
        }
    }

    private fun onMazeLoaded(mazeInfo: MazeHistory) {
        mazeNameState.value = mazeInfo.name.ifEmpty { "???" }
        mazeSizeState.value = mazeInfo.level
        mazeTimeState.value = mazeInfo.timeDisplay
        mazeStepsState.value = mazeInfo.pathLength.toString()
        mazeOverviewState.update(mazeInfo.maze, mazeInfo.path)
    }

}
