/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.lollipop.wear.maze.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.lollipop.play.core.kit.MazeSizeActivity
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.presentation.theme.MazeWearTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(R.style.MazeTheme_Play)
        setContent {
            WearApp("Android")
        }
    }


    @Composable
    fun WearApp(greetingName: String) {
        MazeWearTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                TimeText()
                Greeting(greetingName = greetingName)
            }
        }
    }

    @Composable
    fun Greeting(greetingName: String) {
        Button(
            onClick = {
//                PlayActivity.newMaze(this@MainActivity, 10)
                startActivity(Intent(this@MainActivity, MazeSizeActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth(),
            content = {
                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.hello_world, greetingName)
                )
            }
        )
    }
}
