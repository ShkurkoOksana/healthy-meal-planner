package ksu.katara.healthymealplanner.mvvm.model.sqlite

class AppSQLiteContract {

    object DietTipChaptersTable {
        const val TABLE_NAME = "diet_tip_chapters"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }

    object DietTipsTable {
        const val TABLE_NAME = "diet_tips"
        const val COLUMN_ID = "id"
        const val COLUMN_PHOTO = "photo"
        const val COLUMN_NAME = "name"
        const val COLUMN_DIET_TIP_DETAILS_ID = "diet_tip_details_id"
        const val COLUMN_CHAPTER_ID = "chapter_id"
    }

    object DietTipDetailsTable {
        const val TABLE_NAME = "diet_tip_details"
        const val COLUMN_ID = "id"
        const val COLUMN_BACKGROUND = "background"
    }

    object DietTipDetailStepsTable {
        const val TABLE_NAME = "diet_tip_detail_steps"
        const val COLUMN_ID = "id"
        const val COLUMN_INDEX_NUMBER = "index_number"
        const val COLUMN_TITLE_NAME = "title_name"
        const val COLUMN_TITLE_DESCRIPTION = "title_description"
        const val COLUMN_DIET_TIP_DETAILS_ID = "diet_tip_details_id"
    }

    object RecipeCategoriesTable {
        const val TABLE_NAME = "recipe_categories"
        const val COLUMN_ID = "id"
        const val COLUMN_PHOTO = "photo"
        const val COLUMN_NAME = "name"
    }

    object RecipeCuisineTypesTable {
        const val TABLE_NAME = "recipe_cousine_types"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }

    object RecipeTypesTable {
        const val TABLE_NAME = "recipe_types"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }

    object RecipeIngredientsTable {
        const val TABLE_NAME = "recipe_ingredients"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }

    object IngredientMeasuresTable {
        const val TABLE_NAME = "ingredient_measures"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }

    object RecipePreparationStepsTable {
        const val TABLE_NAME = "recipe_preparation_steps"
        const val COLUMN_ID = "id"
        const val COLUMN_PHOTO = "photo"
        const val COLUMN_DESCRIPTION = "description"
    }

    object RecipesTable {
        const val TABLE_NAME = "recipes"
        const val COLUMN_ID = "id"
        const val COLUMN_CATEGORY_ID = "category_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHOTO = "photo"
        const val COLUMN_CUISINE_TYPE_ID = "cousine_type_id"
        const val COLUMN_ENERGETIC_VALUE = "energetic_value"
        const val COLUMN_PROTEINS = "proteins"
        const val COLUMN_FATS = "fats"
        const val COLUMN_CARBOHYDRATES = "carbohydrates"
        const val COLUMN_IS_FAVORITE = "is_favorite"
    }

    object MealTypesTable {
        const val TABLE_NAME = "meal_types"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }

    object MealPlanTable {
        const val TABLE_NAME = "meal_plan"
        const val COLUMN_ID = "id"
        const val COLUMN_DATE = "date"
    }

    object RecipesRecipeTypesJoinTable {
        const val TABLE_NAME = "jt_recipes_recipe_types"
        const val COLUMN_RECIPE_ID = "recipe_id"
        const val COLUMN_RECIPE_TYPE_ID = "recipe_type_id"
    }

    object RecipesIngredientsJoinTable {
        const val TABLE_NAME = "jt_recipes_ingredients"
        const val COLUMN_RECIPE_ID = "recipe_id"
        const val COLUMN_INGREDIENT_ID = "ingredient_id"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_IS_IN_SHOPPING_LIST = "is_in_shopping_list"
        const val COLUMN_TOTAL_SUM_IS_IN_SHOPPING_LIST = "total_sum_is_in_shopping_list"
    }

    object RecipesPreparationStepsJoinTable {
        const val TABLE_NAME = "jt_recipes_preparation_steps"
        const val COLUMN_RECIPE_ID = "recipe_id"
        const val COLUMN_PREPARATION_STEP_ID = "preparation_step_id"
    }

    object MealPlanRecipesJoinTable {
        const val TABLE_NAME = "jt_meal_plan_recipes"
        const val COLUMN_MEAL_PLAN_ID = "meal_plan_id"
        const val COLUMN_RECIPE_ID = "recipe_id"
    }

    object MealPlanMealTypesJoinTable {
        const val TABLE_NAME = "jt_meal_plan_meal_types"
        const val COLUMN_MEAL_PLAN_ID = "meal_plan_id"
        const val COLUMN_MEAL_TYPE_ID = "meal_type_id"
    }

    object RecipeIngredientsIngredientMeasuresJoinTable {
        const val TABLE_NAME = "jt_recipe_ingredients_ingredient_measures"
        const val COLUMN_RECIPE_INGREDIENT_ID = "recipe_ingredient_id"
        const val COLUMN_INGREDIENT_MEASURE_ID = "ingredient_measure_id"
    }

}