package ksu.katara.healthymealplanner.mvvm.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ksu.katara.healthymealplanner.mvvm.model.calendar.CalendarRepository
import ksu.katara.healthymealplanner.mvvm.model.calendar.InMemoryCalendarRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.RoomDietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.RoomMealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.RecipeCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.RoomRecipeCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.RoomRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.RoomShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.ShoppingListRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindDietTipsRepository(
        roomDietTipsRepository: RoomDietTipsRepository
    ): DietTipsRepository

    @Binds
    abstract fun bindCalendarRepository(
        inMemoryCalendarRepository: InMemoryCalendarRepository
    ): CalendarRepository

    @Binds
    abstract fun bindMealPlanForDateRecipesRepository(
        roomMealPlanForDateRecipesRepository: RoomMealPlanForDateRecipesRepository
    ): MealPlanForDateRecipesRepository

    @Binds
    abstract fun bindRecipeCategoriesRepository(
        roomRecipeCategoriesRepository: RoomRecipeCategoriesRepository
    ): RecipeCategoriesRepository

    @Binds
    abstract fun bindRecipesRepository(
        roomRecipesRepository: RoomRecipesRepository
    ): RecipesRepository

    @Binds
    abstract fun bindShoppingListRepositoryRepository(
        roomShoppingListRepository: RoomShoppingListRepository
    ): ShoppingListRepository

}