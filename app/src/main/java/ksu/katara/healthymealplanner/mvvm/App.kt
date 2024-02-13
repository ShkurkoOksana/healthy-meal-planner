package ksu.katara.healthymealplanner.mvvm

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import ksu.katara.healthymealplanner.foundation.BaseApplication
import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.calendar.InMemoryCalendarRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.RoomDietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.RoomMealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.RoomRecipeCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.RoomRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.room.AppDatabase
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.RoomShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.ShoppingListRepository

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    private lateinit var roomDatabase: AppDatabase

    private lateinit var dietTipsRepository: Repository
    private lateinit var recipeCategoriesRepository: Repository
    private lateinit var recipesRepository: RoomRecipesRepository
    private lateinit var mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository
    private lateinit var shoppingListRepository: ShoppingListRepository

    private val calendarRepository = InMemoryCalendarRepository()

    private val ioDispatcher = IoDispatcher(Dispatchers.IO)
    private val workerDispatcher = IoDispatcher(Dispatchers.Default)

    /**
     * Place your repositories here
     */
    override val singletonScopeDependencies: List<Any> by lazy {
        listOf(
            dietTipsRepository,
            recipeCategoriesRepository,
            recipesRepository,
            mealPlanForDateRecipesRepository,
            shoppingListRepository,
            calendarRepository,
        )
    }

    override fun onCreate() {
        super.onCreate()
        initRoomDatabase()
        dietTipsRepository =
            RoomDietTipsRepository(roomDatabase.getDietTipsDao(), ioDispatcher)
        recipeCategoriesRepository =
            RoomRecipeCategoriesRepository(roomDatabase.getRecipeCategoriesDao(), ioDispatcher)
        recipesRepository = RoomRecipesRepository(roomDatabase.getRecipesDao(), ioDispatcher)
        mealPlanForDateRecipesRepository =
            RoomMealPlanForDateRecipesRepository(
                roomDatabase.getMealPlanDao(),
                roomDatabase.getRecipesDao(),
                ioDispatcher
            )
        shoppingListRepository =
            RoomShoppingListRepository(
                roomDatabase.getShoppingListDao(),
                roomDatabase.getRecipesDao(),
                ioDispatcher
            )
    }

    private fun initRoomDatabase() {
        roomDatabase =
            Room.databaseBuilder(this, AppDatabase::class.java, "database.db")
                .createFromAsset("temp_room.db")
                .build()
    }

}