package yc.dev.goods.ui.screen.goodslist

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import yc.dev.goods.data.model.Good
import yc.dev.goods.data.model.GoodsList
import yc.dev.goods.data.model.Promo
import yc.dev.goods.ui.theme.GoodsTheme
import yc.dev.goods.ui.util.getScreenWidth

@Composable
fun GoodsListScreen(
    goodsListViewModel: GoodsListViewModel = viewModel(),
) {
    val nowUiState by goodsListViewModel.goodsListUiState.collectAsState()
    val uiState: GoodsListUiState.Success = nowUiState as? GoodsListUiState.Success ?: return
    GoodsList(uiState = uiState)
}

@Composable
fun GoodsList(
    modifier: Modifier = Modifier,
    uiState: GoodsListUiState.Success
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.systemBars))

        PromoGrid(uiState.goodsList.promos)

        for (idx in 0..49) {
            Text(
                text = "Hello $idx!",
                modifier = modifier.align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
            )
            if (idx >= 26) break
            Spacer(modifier = modifier.height(itemSpacing))
        }

        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
private fun PromoGrid(
    promos: List<Promo>,
) {
    val promoGridHeight = getScreenWidth()

    val column = 3
    LazyVerticalGrid(
        columns = GridCells.Fixed(column),
        modifier = Modifier.height(promoGridHeight),
        contentPadding = PaddingValues(itemSpacing),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        userScrollEnabled = false,
    ) {
        items(promos.size) {
            Promo(promo = promos[it], index = it)
        }
    }
}

@Composable
private fun Promo(
    promo: Promo,
    index: Int,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Black)
    ) {
        Text(
            text = promo.title,
            modifier = Modifier.align(getPromoTextAlignment(index)),
        )
    }
}

private fun getPromoTextAlignment(index: Int) = when (index % 3) {
    0 -> Alignment.CenterStart
    1 -> Alignment.Center
    2 -> Alignment.CenterEnd
    else -> Alignment.Center
}

private val itemSpacing = 16.dp

@Preview(showBackground = true)
@Composable
fun GoodsListPreview() {
    val fakeUiState: GoodsListUiState.Success = GoodsListUiState.Success(
        GoodsList(
            promos = (0..8).map { Promo("promo") },
            goods = (0..29).map {
                Good(
                    id = it,
                    title = "goods",
                    isLike = false,
                )
            }
        )
    )

    GoodsTheme {
        GoodsList(uiState = fakeUiState)
    }
}