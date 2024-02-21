package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import androidx.room.Embedded

data class RecipesAndCuisineTypesTuple(
    @Embedded val recipeDBEntity: RecipeDBEntity,
    @Embedded val cuisineTypeDBEntity: RecipeCuisineTypeDBEntity
)

data class RecipesAndRecipeTypesTuple(
    @Embedded val recipeDBEntity: RecipeDBEntity,
    @Embedded val recipeTypesJoinTableDBEntity: RecipesRecipeTypesJoinTableDBEntity,
    @Embedded val recipeTypeDBEntity: RecipeTypeDBEntity
)

data class RecipesAndIngredientsTuple(
    @Embedded val recipeDBEntity: RecipeDBEntity,
    @Embedded val recipesRecipeIngredientsJoinTableDBEntity: RecipesRecipeIngredientsJoinTableDBEntity,
    @Embedded val recipeIngredientDBEntity: RecipeIngredientDBEntity,
    @Embedded val recipeIngredientsIngredientMeasuresJoinTableDBEntity: RecipeIngredientsIngredientMeasuresJoinTableDBEntity,
    @Embedded val ingredientMeasureDBEntity: RecipeIngredientMeasureDBEntity
)

data class RecipesAndPreparationStepsTuple(
    @Embedded val recipeDBEntity: RecipeDBEntity,
    @Embedded val recipesPreparationStepsJoinTableDBEntity: RecipesPreparationStepsJoinTableDBEntity,
    @Embedded val preparationStepDBEntity: RecipePreparationStepDBEntity
)
