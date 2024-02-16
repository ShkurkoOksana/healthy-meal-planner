package ksu.katara.healthymealplanner.mvvm.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.DietTipsDao
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.MealPlanDao
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.room.RecipeCategoriesDao
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.RecipesDao
import ksu.katara.healthymealplanner.mvvm.model.room.AppDatabase
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.room.ShoppingListDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomDatabaseModule {

    @Provides
    @Singleton
    fun getAppDB(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "database.db")
            .createFromAsset("temp_room.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideDietTipsDao(appDB: AppDatabase): DietTipsDao {
        return appDB.getDietTipsDao()
    }

    @Provides
    @Singleton
    fun provideMealPlanDao(appDB: AppDatabase): MealPlanDao {
        return appDB.getMealPlanDao()
    }

    @Provides
    @Singleton
    fun provideRecipeCategoriesDao(appDB: AppDatabase): RecipeCategoriesDao {
        return appDB.getRecipeCategoriesDao()
    }

    @Provides
    @Singleton
    fun provideRecipesDao(appDB: AppDatabase): RecipesDao {
        return appDB.getRecipesDao()
    }

    @Provides
    @Singleton
    fun provideShoppingListDao(appDB: AppDatabase): ShoppingListDao {
        return appDB.getShoppingListDao()
    }

}