package ksu.katara.healthymealplanner.mvvm

import ksu.katara.healthymealplanner.mvvm.model.addrecipes.AddRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.addrecipes.InMemoryAddRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.calendar.CalendarRepository
import ksu.katara.healthymealplanner.mvvm.model.calendar.InMemoryCalendarRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.InMemoryDietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.InMemoryMealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.product.InMemoryProductsRepository
import ksu.katara.healthymealplanner.mvvm.model.product.ProductsRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.CategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.InMemoryCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.InMemoryRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.InMemoryShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.ShoppingListRepository

object Repositories {

    val dietTipsRepository: DietTipsRepository = InMemoryDietTipsRepository()

    private val productsRepository: ProductsRepository = InMemoryProductsRepository()

    val recipeCategoriesRepository: CategoriesRepository = InMemoryCategoriesRepository()

    val recipesRepository: RecipesRepository = InMemoryRecipesRepository(
        productsRepository,
    )

    val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository = InMemoryMealPlanForDateRecipesRepository()

    val addRecipesRepository: AddRecipesRepository = InMemoryAddRecipesRepository(
        recipesRepository,
        mealPlanForDateRecipesRepository,
    )

    val shoppingListRepository: ShoppingListRepository = InMemoryShoppingListRepository(recipesRepository)

    val calendarRepository: CalendarRepository = InMemoryCalendarRepository()
}