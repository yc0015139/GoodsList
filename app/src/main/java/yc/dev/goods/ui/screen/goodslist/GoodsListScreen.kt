package yc.dev.goods.ui.screen.goodslist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import yc.dev.goods.ui.theme.GoodsTheme

@Composable
fun GoodsList(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
        for (it in 0 ..49) {
            Text(
                text = "Hello $it!",
                modifier = modifier.align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
            )
            if (it >= 49) break

            Spacer(modifier = modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }

}

@Preview(showBackground = true)
@Composable
fun GoodsListPreview() {
    GoodsTheme {
        GoodsList()
    }
}