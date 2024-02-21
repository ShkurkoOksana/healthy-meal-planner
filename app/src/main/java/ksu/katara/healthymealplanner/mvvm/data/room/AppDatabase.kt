package ksu.katara.healthymealplanner.mvvm.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ksu.katara.healthymealplanner.mvvm.data.dietTips.DietTipsDao
import ksu.katara.healthymealplanner.mvvm.data.dietTips.entities.DietTipChapterDBEntity
import ksu.katara.healthymealplanner.mvvm.data.dietTips.entities.DietTipDBEntity
import ksu.katara.healthymealplanner.mvvm.data.dietTips.entities.DietTipDetailDBEntity
import ksu.katara.healthymealplanner.mvvm.data.dietTips.entities.DietTipDetailStepDBEntity
import ksu.katara.healthymealplanner.mvvm.data.mealplan.MealPlanDao
import ksu.katara.healthymealplanner.mvvm.data.mealplan.entities.MealPlanDBEntity
import ksu.katara.healthymealplanner.mvvm.data.mealplan.entities.MealPlanRecipesJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.data.mealplan.entities.MealTypeDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipecategories.RecipeCategoriesDao
import ksu.katara.healthymealplanner.mvvm.data.recipecategories.entities.RecipeCategoryDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.RecipesDao
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeCuisineTypeDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeIngredientDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeIngredientMeasureDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeIngredientsIngredientMeasuresJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipePreparationStepDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeTypeDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipesPreparationStepsJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipesRecipeIngredientsJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipesRecipeTypesJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.data.shoppinglist.ShoppingListDao
import javax.inject.Singleton

@Database(
    version = 3,
    entities = [
        DietTipChapterDBEntity::class,
        DietTipDBEntity::class,
        DietTipDetailDBEntity::class,
        DietTipDetailStepDBEntity::class,
        RecipeCategoryDBEntity::class,
        RecipeDBEntity::class,
        RecipeCuisineTypeDBEntity::class,
        RecipeTypeDBEntity::class,
        RecipeIngredientDBEntity::class,
        RecipeIngredientMeasureDBEntity::class,
        RecipePreparationStepDBEntity::class,
        RecipesRecipeTypesJoinTableDBEntity::class,
        RecipesRecipeIngredientsJoinTableDBEntity::class,
        RecipeIngredientsIngredientMeasuresJoinTableDBEntity::class,
        RecipesPreparationStepsJoinTableDBEntity::class,
        MealPlanDBEntity::class,
        MealTypeDBEntity::class,
        MealPlanRecipesJoinTableDBEntity::class,
    ]
)
@Singleton
abstract class AppDatabase : RoomDatabase() {

    abstract fun getDietTipsDao(): DietTipsDao

    abstract fun getRecipeCategoriesDao(): RecipeCategoriesDao

    abstract fun getRecipesDao(): RecipesDao

    abstract fun getMealPlanDao(): MealPlanDao

    abstract fun getShoppingListDao(): ShoppingListDao

}