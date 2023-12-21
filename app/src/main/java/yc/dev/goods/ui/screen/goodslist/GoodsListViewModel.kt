package yc.dev.goods.ui.screen.goodslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import yc.dev.goods.data.model.Good
import yc.dev.goods.data.model.GoodsList
import yc.dev.goods.data.repository.GoodsRepository
import javax.inject.Inject

@HiltViewModel
class GoodsListViewModel @Inject constructor(
    goodsRepository: GoodsRepository,
) : ViewModel() {

    private val _goods: MutableMap<Int, Good> = mutableMapOf()

    val goodsListUiState: StateFlow<GoodsListUiState> = goodsRepository.fetchData().map {
        it.goods.forEach { (id, good) -> _goods[id] = good }
        GoodsListUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GoodsListUiState.Loading,
    )

    private val _filterEvent: MutableSharedFlow<Map<Int, Good>> = MutableSharedFlow()
    val filterEvent: SharedFlow<Map<Int, Good>> = _filterEvent.asSharedFlow()

    private val _remainingGoodsCountEvent: MutableSharedFlow<Int> = MutableSharedFlow()
    val remainingGoodsCountEvent: SharedFlow<Int> = _remainingGoodsCountEvent.asSharedFlow()

    fun changeFilter() {
        viewModelScope.launch {
            _filterEvent.emit(_goods)
        }
    }

    fun updateLikedState(good: Good) {
        _goods[good.id]?.let {
            _goods[good.id] = good
        } ?: throw IllegalStateException("Good not found")
    }

    fun updateRemainingCount(currentIndex: Int) {
        viewModelScope.launch {
            val remainingGoodsCount = _goods.size - currentIndex - 1
            _remainingGoodsCountEvent.emit(remainingGoodsCount)
        }
    }
}

sealed interface GoodsListUiState {
    object Loading : GoodsListUiState
    data class Success(val goodsList: GoodsList) : GoodsListUiState
}