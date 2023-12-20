package yc.dev.goods.ui.screen.goodslist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import yc.dev.goods.data.model.Good
import yc.dev.goods.data.model.GoodsList
import yc.dev.goods.data.model.Promo
import yc.dev.goods.ui.theme.GoodsTheme
import yc.dev.goods.ui.util.ObserverAsEvent
import yc.dev.goods.ui.util.getScreenWidth

@Composable
fun GoodsListScreen(
    goodsListViewModel: GoodsListViewModel = viewModel(),
) {
    val nowUiState by goodsListViewModel.goodsListUiState.collectAsState()
    val uiState: GoodsListUiState.Success = nowUiState as? GoodsListUiState.Success ?: return

    val density = LocalDensity.current
    val statusBarHeightInPx = WindowInsets.statusBars.getTop(density)
    val statusBarHeight = with(density) { statusBarHeightInPx.toDp() }
    val listState = rememberLazyListState()
    val isSticky = remember(statusBarHeightInPx) {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.any {
                val earlyResponseRadio = 1.5
                it.key == KEY_FILTER_ITEM && it.offset < statusBarHeightInPx * earlyResponseRadio
            }
        }
    }

    GoodsList(
        isFilterItemSticky = isSticky.value,
        onFilterClick = { goodsListViewModel.changeFilter() },
        filterEvent = goodsListViewModel.filterEvent,
        statusBarHeight = statusBarHeight,
        listState = listState,
        uiState = uiState,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoodsList(
    modifier: Modifier = Modifier,
    isFilterItemSticky: Boolean,
    onFilterClick: () -> Unit,
    filterEvent: SharedFlow<Unit>,
    statusBarHeight: Dp,
    listState: LazyListState,
    uiState: GoodsListUiState.Success,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = listState,
    ) {

        item {
            PromoGrid(
                promos = uiState.goodsList.promos,
                statusBarHeight = statusBarHeight,
            )
        }

        stickyHeader(key = KEY_FILTER_ITEM) {
            Spacer(modifier = Modifier.size(if (isFilterItemSticky) statusBarHeight else 0.dp))
            FilterItem(
                onFilterClick = onFilterClick,
            )
        }

        item {
            GoodsBlock(
                filterEvent = filterEvent,
            )
        }

        item {
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun PromoGrid(
    promos: List<Promo>,
    statusBarHeight: Dp,
) {
    val promoGridHeight = getScreenWidth() + statusBarHeight

    val column = 3
    LazyVerticalGrid(
        columns = GridCells.Fixed(column),
        modifier = Modifier
            .height(promoGridHeight)
            .padding(top = statusBarHeight),
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

@Composable
fun FilterItem(
    onFilterClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(getItemSize())
            .padding(horizontal = itemSpacing)
            .background(color = Color.LightGray)
            .border(width = 1.dp, color = Color.Black),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "filter",
        )

        Icon(
            modifier = Modifier
                .padding(end = itemSpacing)
                .align(Alignment.CenterEnd)
                .clickable(
                    onClick = onFilterClick,
                ),
            imageVector = Icons.Default.List,
            contentDescription = "Filter",
        )
    }
}

@Composable
private fun GoodsBlock(
    filterEvent: SharedFlow<Unit>,
) {
    ObserverAsEvent(filterEvent) {
        // Trigger event here
    }

    for (idx in 0..49) {
        Text(
            text = "Hello $idx!",
            modifier = Modifier,
            fontSize = 20.sp,
        )
        if (idx >= 26) break
        Spacer(modifier = Modifier.height(itemSpacing))
    }
}

@Composable
private fun getItemSize(): Dp = (getScreenWidth() - itemSpacing * 4) / 3

private val itemSpacing = 16.dp
private const val KEY_FILTER_ITEM = "FilterItem"

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
    val fakeSharedFlow: SharedFlow<Unit> = MutableSharedFlow()

    GoodsTheme {
        GoodsList(
            isFilterItemSticky = false,
            onFilterClick = { },
            filterEvent = fakeSharedFlow,
            statusBarHeight = 16.dp,
            listState = rememberLazyListState(),
            uiState = fakeUiState,
        )
    }
}
