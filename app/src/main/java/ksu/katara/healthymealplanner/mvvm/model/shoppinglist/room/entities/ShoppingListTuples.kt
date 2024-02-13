package ksu.katara.healthymealplanner.mvvm.model.shoppinglist.room.entities

import androidx.room.Embedded
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeIngredientDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeIngredientMeasureDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipeIngredientsIngredientMeasuresJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.entities.RecipesRecipeIngredientsJoinTableDBEntity

data class ShoppingListTuples(
    @Embedded val recipeDBEntity: RecipeDBEntity,
    @Embedded val recipeIngredientsJoinTableDBEntity: RecipesRecipeIngredientsJoinTableDBEntity,
    @Embedded val recipeIngredientDBEntity: RecipeIngredientDBEntity,
    @Embedded val recipeIngredientsIngredientMeasuresJoinTableDBEntity: RecipeIngredientsIngredientMeasuresJoinTableDBEntity,
    @Embedded val ingredientMeasureDBEntity: RecipeIngredientMeasureDBEntity
)