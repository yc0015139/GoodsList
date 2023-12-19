package yc.dev.goods.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun getScreenWidth(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}