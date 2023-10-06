package ksu.katara.healthymealplanner.mvvm.model.product

import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.model.product.entities.Product


/**
 * Repository of products interface.
 *
 * Provides access to the available products.
 */
interface ProductsRepository : Repository {

    /**
     * Get available product by id.
     */
    fun getById(id: Long): Product

}