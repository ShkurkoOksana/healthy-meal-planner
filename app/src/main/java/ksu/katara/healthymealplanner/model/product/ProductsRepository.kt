package ksu.katara.healthymealplanner.model.product

import ksu.katara.healthymealplanner.model.product.entities.Product

interface ProductsRepository {

    fun getProductById(id: Long): Product

}