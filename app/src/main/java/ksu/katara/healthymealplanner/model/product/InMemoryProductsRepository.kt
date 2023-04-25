package ksu.katara.healthymealplanner.model.product

import ksu.katara.healthymealplanner.exceptions.ProductNotFoundException
import ksu.katara.healthymealplanner.model.product.entities.Product

const val TAG = "InMemoryRecipesItemListRepository"

class InMemoryProductsRepository : ProductsRepository {
    private val products = mutableListOf<Product>()

    init {
        PRODUCTS.forEach { (id, name) ->
            var product = Product(
                id = id.toLong(),
                name = name,
            )

            products.add(product)
        }
    }

    override fun getProductById(id: Long): Product {
        return products.firstOrNull { it.id == id } ?: throw ProductNotFoundException()
    }

    companion object {
        private val PRODUCTS = mapOf(
            0 to "Яйцо",
            1 to "Топленное масло",
            2 to "Сыр Фета",
            4 to "Болгарский перец",
            5 to "Помидор",
            6 to "Огурец",
            7 to "Салат-латук",
            8 to "Репчатый лук",
            9 to "Маслины",
            10 to "Лимон",
            11 to "Оливковое масло",
            12 to "Смесь трав",
            13 to "Соль",
            14 to "Черный перец молотый",
            15 to "Креветки",
            16 to "Листья салата",
            17 to "Помидоры черри",
            18 to "Чеснок",
            19 to "Белый хлеб",
            20 to "Бальзамический уксус",
            21 to "Горчица",
            22 to "Вода",
            23 to "Говядина",
            24 to "Свекла",
            25 to "Морковь",
            26 to "Томатная паста",
            27 to "Белокачанная капуста",
            28 to "Картофель",
            29 to "Лавровый лист",
            30 to "Петрушка",
            31 to "Укроп",
            32 to "Минтай",
            33 to "Мука зеленой гречки",
        )
    }
}