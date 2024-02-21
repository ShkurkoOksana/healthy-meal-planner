package ksu.katara.healthymealplanner.mvvm.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ksu.katara.healthymealplanner.mvvm.data.calendar.InMemoryCalendarRepository
import ksu.katara.healthymealplanner.mvvm.data.dietTips.RoomDietTipsRepository
import ksu.katara.healthymealplanner.mvvm.data.mealplan.RoomMealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.data.recipecategories.RoomRecipeCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.data.recipes.RoomRecipesRepository
import ksu.katara.healthymealplanner.mvvm.domain.calendar.CalendarRepository
import ksu.katara.healthymealplanner.mvvm.domain.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.domain.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.domain.recipecategories.RecipeCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.domain.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.data.shoppinglist.RoomShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.domain.shoppinglist.ShoppingListRepository

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