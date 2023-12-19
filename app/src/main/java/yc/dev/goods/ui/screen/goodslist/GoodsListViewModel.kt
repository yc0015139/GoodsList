package yc.dev.goods.ui.screen.goodslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import yc.dev.goods.data.model.GoodsList
import yc.dev.goods.data.repository.GoodsRepository
import javax.inject.Inject

@HiltViewModel
class GoodsListViewModel @Inject constructor(
    goodsRepository: GoodsRepository,
) : ViewModel() {

    val goodsListUiState: StateFlow<GoodsListUiState> = goodsRepository.fetchData().map {
        GoodsListUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GoodsListUiState.Loading,
    )

}

sealed interface GoodsListUiState {
    object Loading : GoodsListUiState
    data class Success(val goodsList: GoodsList) : GoodsListUiState
}