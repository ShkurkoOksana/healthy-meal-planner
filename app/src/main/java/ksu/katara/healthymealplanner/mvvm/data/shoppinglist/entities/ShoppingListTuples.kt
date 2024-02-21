package ksu.katara.healthymealplanner.mvvm.data.shoppinglist.entities

import androidx.room.Embedded
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeIngredientDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeIngredientMeasureDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipeIngredientsIngredientMeasuresJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.data.recipes.entities.RecipesRecipeIngredientsJoinTableDBEntity

data class ShoppingListTuples(
    @Embedded val recipeDBEntity: RecipeDBEntity,
    @Embedded val recipeIngredientsJoinTableDBEntity: RecipesRecipeIngredientsJoinTableDBEntity,
    @Embedded val recipeIngredientDBEntity: RecipeIngredientDBEntity,
    @Embedded val recipeIngredientsIngredientMeasuresJoinTableDBEntity: RecipeIngredientsIngredientMeasuresJoinTableDBEntity,
    @Embedded val ingredientMeasureDBEntity: RecipeIngredientMeasureDBEntity
)