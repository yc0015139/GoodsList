package yc.dev.goods.data.model

data class GoodsList(
    val promos: List<Promo>,
    val goods: List<Good>
)

data class Promo(
    val title: String
)
data class Good(
    val id: Int,
    val title: String,
    val isLike: Boolean,
)
