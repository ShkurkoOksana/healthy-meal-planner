package ksu.katara.healthymealplanner

import ksu.katara.healthymealplanner.model.addrecipephoto.InMemoryAddRecipesPhotoRepository
import ksu.katara.healthymealplanner.model.addrecipephoto.entities.AddRecipesPhotoRepository
import ksu.katara.healthymealplanner.model.addrecipes.AddRecipesRepository
import ksu.katara.healthymealplanner.model.addrecipes.InMemoryAddRecipesRepository
import ksu.katara.healthymealplanner.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.model.dietTips.InMemoryDietTipsRepository
import ksu.katara.healthymealplanner.model.mealplanfortoday.InMemoryMealPlanForTodayRecipesRepository
import ksu.katara.healthymealplanner.model.mealplanfortoday.MealPlanForTodayRecipesRepository
import ksu.katara.healthymealplanner.model.product.InMemoryProductsRepository
import ksu.katara.healthymealplanner.model.product.ProductsRepository
import ksu.katara.healthymealplanner.model.recipes.InMemoryRecipesRepository
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.shoppinglist.InMemoryShoppingListRepository
import ksu.katara.healthymealplanner.model.shoppinglist.ShoppingListRepository

object Repositories {

    val dietTipsRepository: DietTipsRepository = InMemoryDietTipsRepository()

    val productsRepository: ProductsRepository = InMemoryProductsRepository()

    val recipesRepository: RecipesRepository = InMemoryRecipesRepository(productsRepository)

    val mealPlanForTodayRecipesRepository: MealPlanForTodayRecipesRepository = InMemoryMealPlanForTodayRecipesRepository()

    val addRecipePhotoRepository: AddRecipesPhotoRepository = InMemoryAddRecipesPhotoRepository(recipesRepository)

    val addRecipesRepository: AddRecipesRepository = InMemoryAddRecipesRepository(recipesRepository)

    val shoppingListRepository: ShoppingListRepository = InMemoryShoppingListRepository()
}