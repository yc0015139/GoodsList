package yc.dev.goods.ui.screen.goodslist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
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
    val navigationBarHeightInPx = WindowInsets.statusBars.getTop(density)
    val navigationBarHeight = with(density) { navigationBarHeightInPx.toDp() }
    val listState = rememberLazyListState()
    val isSticky = remember(statusBarHeightInPx) {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.any {
                val earlyResponseRadio = 1.5
                it.key == KEY_FILTER_ITEM && it.offset < statusBarHeightInPx * earlyResponseRadio
            }
        }
    }

    GoodsListContent(
        isFilterItemSticky = isSticky.value,
        onFilterClicked = { goodsListViewModel.changeFilter() },
        onLikeClicked = { good -> goodsListViewModel.updateLikedState(good) },
        onUpdateRemainingCount = { idx -> goodsListViewModel.updateRemainingCount(idx) },
        remainingGoodsCountEvent = goodsListViewModel.remainingGoodsCountEvent,
        filterEvent = goodsListViewModel.filterEvent,
        statusBarHeight = statusBarHeight,
        navigationBarHeight = navigationBarHeight,
        listState = listState,
        uiState = uiState,
    )
}

@Composable
fun GoodsListContent(
    modifier: Modifier = Modifier,
    isFilterItemSticky: Boolean,
    onFilterClicked: () -> Unit,
    onLikeClicked: (Good) -> Unit,
    onUpdateRemainingCount: (Int) -> Unit,
    filterEvent: SharedFlow<Map<Int, Good>>,
    remainingGoodsCountEvent: SharedFlow<Int>,
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    listState: LazyListState,
    uiState: GoodsListUiState.Success,
) {
    val remainingGoodsCount = remember { mutableStateOf(0) }
    ObserverAsEvent(remainingGoodsCountEvent) {
        remainingGoodsCount.value = it
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        GoodsList(
            modifier,
            isFilterItemSticky,
            onFilterClicked,
            onLikeClicked,
            onUpdateRemainingCount,
            filterEvent,
            statusBarHeight,
            listState,
            uiState,
        )

        if (isFilterItemSticky) {
            BottomRemainingCountItem(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                navigationBarHeight,
                remainingGoodsCount.value,
            )
        }
    }
}

@Composable
private fun BottomRemainingCountItem(
    modifier: Modifier,
    navigationBarHeight: Dp,
    count: Int,
) {
    Box(
        modifier = modifier
            .padding(bottom = itemSpacing / 2 + navigationBarHeight)
            .height(getItemSize())
            .aspectRatio(1.75f)
            .background(color = Color.LightGray)
            .border(width = 1.dp, color = Color.Black),
    ) {
        Text(
            text = "剩餘 $count 項產品",
            modifier = Modifier
                .align(Alignment.Center)
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            modifier = Modifier
                .rotate(180f)
                .padding(itemSpacing)
                .align(Alignment.BottomCenter)
                .clickable {

                },
            contentDescription = "ToTop",
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoodsList(
    modifier: Modifier = Modifier,
    isFilterItemSticky: Boolean,
    onFilterClicked: () -> Unit,
    onLikeClicked: (Good) -> Unit,
    onUpdateRemainingCount: (Int) -> Unit,
    filterEvent: SharedFlow<Map<Int, Good>>,
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
                onFilterClicked = onFilterClicked,
            )
        }

        item {
            GoodsBlock(
                defaultGoods = uiState.goodsList.goods,
                filterEvent = filterEvent,
                onLikeClicked = onLikeClicked,
                onUpdateRemainingCount = onUpdateRemainingCount,
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

    val column = THREE_COLUMNS
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
    onFilterClicked: () -> Unit,
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
                    onClick = onFilterClicked,
                ),
            imageVector = Icons.Default.List,
            contentDescription = "Filter",
        )
    }
}

@Composable
private fun GoodsBlock(
    defaultGoods: Map<Int, Good>,
    filterEvent: SharedFlow<Map<Int, Good>>,
    onLikeClicked: (Good) -> Unit,
    onUpdateRemainingCount: (Int) -> Unit,
) {
    val isGrid = remember { mutableStateOf(true) }
    val rememberGoods = rememberSaveable { mutableStateOf(defaultGoods) }
    ObserverAsEvent(filterEvent) {
        isGrid.value = !isGrid.value
        rememberGoods.value = it
    }
    val goods = rememberGoods.value
    val (width, height) = calculateDimensions(isGrid.value, getScreenWidth())
    val column = if (isGrid.value) TWO_COLUMNS else ONE_COLUMN
    val blockHeight =
        if (isGrid.value) goods.size / TWO_COLUMNS * (height + itemSpacing) + itemSpacing
        else goods.size * (height + itemSpacing)

    LazyVerticalGrid(
        columns = GridCells.Fixed(column),
        modifier = Modifier
            .height(blockHeight),
        contentPadding = PaddingValues(itemSpacing),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        userScrollEnabled = false,
    ) {
        items(goods.size) { idx ->
            val good = goods[idx] ?: return@items
            GoodItem(
                targetState = column,
                onLikeClicked = onLikeClicked,
                width, height, good,
            )
        }
    }
}

private fun calculateDimensions(isGrid: Boolean, screenWidth: Dp): Pair<Dp, Dp> {
    val width =
        if (isGrid) (screenWidth - itemSpacing * (TWO_COLUMNS + 1)) / TWO_COLUMNS
        else screenWidth - itemSpacing * (ONE_COLUMN + 1)
    val gridItemRectangleRadio = 1.125f
    val listItemRectangleRadio = 1.77f
    val rectangleRatio = if (isGrid) gridItemRectangleRadio else 1 / listItemRectangleRadio
    val height = width * rectangleRatio
    return Pair(width, height)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun GoodItem(
    targetState: Int,
    onLikeClicked: (Good) -> Unit,
    width: Dp,
    height: Dp,
    good: Good,
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            scaleIn(initialScale = 0.85f, animationSpec = tween(350, delayMillis = 50)) +
                fadeIn(animationSpec = tween(300)) with
                scaleOut(animationSpec = tween(300)) +
                fadeOut(animationSpec = tween(350, delayMillis = 50))
        },
        label = "AnimatedItem",
    ) {
        Box(
            modifier = Modifier
                .size(width, height)
                .border(width = 1.dp, color = Color.Black),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "${good.title} ${good.id}"
            )

            LikeButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onLikeClicked,
                good,
            )
        }
    }
}

@Composable
private fun LikeButton(
    modifier: Modifier,
    onLikeClicked: (Good) -> Unit,
    good: Good,
) {
    val isLiked = remember { mutableStateOf(good.isLiked) }
    Icon(
        imageVector = if (isLiked.value) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
        contentDescription = "Thumb Up",
        modifier = modifier
            .padding(itemSpacing)
            .clickable {
                onLikeClicked.invoke(good.copy(isLiked = !isLiked.value))
                isLiked.value = !isLiked.value
            },
    )
}

@Composable
private fun getItemSize(): Dp = (getScreenWidth() - itemSpacing * 4) / 3

private val itemSpacing = 16.dp
private const val KEY_FILTER_ITEM = "FilterItem"
private const val ONE_COLUMN = 1
private const val TWO_COLUMNS = 2
private const val THREE_COLUMNS = 3

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
                    isLiked = false,
                )
            }.associateBy { it.id }
        )
    )
    val fakeFilterEvent: SharedFlow<Map<Int, Good>> = MutableSharedFlow()
    val fakeCurrentGoodIndexEvent: SharedFlow<Int> = MutableSharedFlow()

    GoodsTheme {
        GoodsListContent(
            isFilterItemSticky = true,
            onFilterClicked = { },
            onLikeClicked = { _ -> },
            onUpdateRemainingCount = { _ -> },
            filterEvent = fakeFilterEvent,
            remainingGoodsCountEvent = fakeCurrentGoodIndexEvent,
            statusBarHeight = 16.dp,
            navigationBarHeight = 16.dp,
            listState = rememberLazyListState(),
            uiState = fakeUiState,
        )
    }
}
