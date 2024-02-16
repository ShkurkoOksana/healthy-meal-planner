package ksu.katara.healthymealplanner.mvvm.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.DietTipsDao
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities.DietTipChapterDBEntity
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities.DietTipDBEntity
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities.DietTipDetailDBEntity
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities.DietTipDetailStepDBEntity
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.MealPlanDao
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities.MealPlanDBEntity
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities.MealPlanRecipesJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities.MealTypeDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.room.RecipeCategoriesDao
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.room.entities.RecipeCategoryDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.RecipesDao
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeCuisineTypeDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeIngredientDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeIngredientMeasureDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeIngredientsIngredientMeasuresJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipePreparationStepDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeTypeDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipesPreparationStepsJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipesRecipeIngredientsJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipesRecipeTypesJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.room.ShoppingListDao
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