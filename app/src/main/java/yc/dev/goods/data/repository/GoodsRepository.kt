package yc.dev.goods.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import yc.dev.goods.data.model.Good
import yc.dev.goods.data.model.GoodsList
import yc.dev.goods.data.model.Promo
import javax.inject.Inject

class GoodsRepository @Inject constructor(

) {

    fun fetchData() = flow {
        val promos = (0..8).map {
            Promo(
                title = "promo"
            )
        }
        val goods: Map<Int, Good> = (0..29).map {
            Good(
                id = it,
                title = "goods",
                isLiked = false,
            )
        }.associateBy { it.id }
        val goodsList = GoodsList(promos, goods)
        emit(goodsList)
    }.flowOn(Dispatchers.IO)

}