package ksu.katara.healthymealplanner

import ksu.katara.healthymealplanner.model.addrecipes.AddRecipesRepository
import ksu.katara.healthymealplanner.model.addrecipes.InMemoryAddRecipesRepository
import ksu.katara.healthymealplanner.model.calendar.CalendarRepository
import ksu.katara.healthymealplanner.model.calendar.InMemoryCalendarRepository
import ksu.katara.healthymealplanner.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.model.dietTips.InMemoryDietTipsRepository
import ksu.katara.healthymealplanner.model.mealplan.InMemoryMealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.model.product.InMemoryProductsRepository
import ksu.katara.healthymealplanner.model.product.ProductsRepository
import ksu.katara.healthymealplanner.model.recipecategories.CategoriesRepository
import ksu.katara.healthymealplanner.model.recipecategories.InMemoryCategoriesRepository
import ksu.katara.healthymealplanner.model.recipes.InMemoryRecipesRepository
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.shoppinglist.InMemoryShoppingListRepository
import ksu.katara.healthymealplanner.model.shoppinglist.ShoppingListRepository

object Repositories {

    val dietTipsRepository: DietTipsRepository = InMemoryDietTipsRepository()

    val productsRepository: ProductsRepository = InMemoryProductsRepository()

    val recipeCategoriesRepository: CategoriesRepository = InMemoryCategoriesRepository()

    val recipesRepository: RecipesRepository = InMemoryRecipesRepository(
        productsRepository,
    )

    val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository = InMemoryMealPlanForDateRecipesRepository()

    val addRecipesRepository: AddRecipesRepository = InMemoryAddRecipesRepository(
        recipesRepository,
        mealPlanForDateRecipesRepository,
    )

    val shoppingListRepository: ShoppingListRepository = InMemoryShoppingListRepository()

    val calendarRepository: CalendarRepository = InMemoryCalendarRepository()
}