package ksu.katara.healthymealplanner.mvvm.model.product

import ksu.katara.healthymealplanner.foundation.tasks.ThreadUtils
import ksu.katara.healthymealplanner.foundation.tasks.factories.TasksFactory
import ksu.katara.healthymealplanner.mvvm.model.ProductNotFoundException
import ksu.katara.healthymealplanner.mvvm.model.product.entities.Product

/**
 * Simple in-memory implementation of [ProductsRepository]
 */
class InMemoryProductsRepository : ProductsRepository {

    private val products = mutableListOf<Product>()
    private val productsSize = NAME.size

    init {
        (0 until productsSize).forEach {
            val product = Product(
                id = it.toLong(),
                name = NAME[it],
            )
            products.add(product)
        }
    }

    override fun getProductById(id: Long): Product {
        return products.firstOrNull { it.id == id } ?: throw ProductNotFoundException()
    }

    companion object {
        private val NAME = listOf(
            "Яйцо",
            "Топленное масло",
            "Сыр Фета",
            "Болгарский перец",
            "Помидор",
            "Огурец",
            "Салат-латук",
            "Репчатый лук",
            "Маслины",
            "Лимон",
            "Оливковое масло",
            "Смесь трав",
            "Соль",
            "Черный перец молотый",
            "Креветки",
            "Листья салата",
            "Помидоры черри",
            "Чеснок",
            "Белый хлеб",
            "Бальзамический уксус",
            "Горчица",
            "Вода",
            "Говядина",
            "Свекла",
            "Морковь",
            "Томатная паста",
            "Белокачанная капуста",
            "Картофель",
            "Лавровый лист",
            "Петрушка",
            "Укроп",
            "Минтай",
            "Мука",
        )
    }
}