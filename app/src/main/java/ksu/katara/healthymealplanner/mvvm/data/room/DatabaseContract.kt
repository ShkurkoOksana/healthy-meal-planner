package ksu.katara.healthymealplanner.mvvm.data.room

class DatabaseContract {

    object DietTipChaptersTable {
        const val TABLE_NAME = "diet_tip_chapters"
    }

    object DietTipsTable {
        const val TABLE_NAME = "diet_tips"
        const val COLUMN_CHAPTER_ID = "diet_tip_chapter_id"
    }

    object DietTipDetailsTable {
        const val TABLE_NAME = "diet_tip_details"
        const val COLUMN_ID = "details_id"
    }

    object DietTipDetailStepsTable {
        const val TABLE_NAME = "diet_tip_detail_steps"
        const val COLUMN_DIET_TIP_DETAILS_ID = "detail_step_details_id"
    }

    object RecipeCategoriesTable {
        const val TABLE_NAME = "recipe_categories"
        const val COLUMN_ID = "category_id"
    }

    object RecipeCuisineTypesTable {
        const val TABLE_NAME = "recipe_cuisine_types"
        const val COLUMN_ID = "cuisine_type_id"
    }

    object RecipeTypesTable {
        const val TABLE_NAME = "recipe_types"
        const val COLUMN_ID = "type_id"
    }

    object RecipeIngredientsTable {
        const val TABLE_NAME = "recipe_ingredients"
        const val COLUMN_ID = "ingredient_id"
    }

    object IngredientMeasuresTable {
        const val TABLE_NAME = "ingredient_measures"
        const val COLUMN_ID = "ingredient_measure_id"
    }

    object RecipePreparationStepsTable {
        const val TABLE_NAME = "recipe_preparation_steps"
        const val COLUMN_ID = "recipe_preparation_step_id"
    }

    object RecipesTable {
        const val TABLE_NAME = "recipes"
        const val COLUMN_ID = "recipe_id"
        const val COLUMN_CATEGORY_ID = "recipe_category_id"
        const val COLUMN_CUISINE_TYPE_ID = "recipe_cuisine_type_id"
    }

    object MealTypesTable {
        const val TABLE_NAME = "meal_types"
        const val COLUMN_ID = "meal_type_id"
        const val COLUMN_NAME = "meal_type_name"
    }

    object MealPlanTable {
        const val TABLE_NAME = "meal_plan"
        const val COLUMN_ID = "meal_plan_id"
        const val COLUMN_DATE = "meal_plan_date"
    }

    object RecipesRecipeTypesJoinTable {
        const val TABLE_NAME = "jt_recipes_recipe_types"
        const val COLUMN_RECIPE_ID = "rt_jt_recipe_id"
        const val COLUMN_RECIPE_TYPE_ID = "rt_jt_recipe_type_id"
    }

    object RecipesIngredientsJoinTable {
        const val TABLE_NAME = "jt_recipes_ingredients"
        const val COLUMN_RECIPE_ID = "ri_jt_recipe_id"
        const val COLUMN_INGREDIENT_ID = "ri_jt_ingredient_id"
        const val COLUMN_IS_IN_SHOPPING_LIST = "ri_jt_is_in_shopping_list"
        const val COLUMN_IS_CROSS_IN_SHOPPING_LIST = "ri_jt_is_cross_in_shopping_list"
        const val COLUMN_TOTAL_SUM_IS_IN_SHOPPING_LIST = "total_sum_is_in_shopping_list"
    }

    object RecipesPreparationStepsJoinTable {
        const val TABLE_NAME = "jt_recipes_preparation_steps"
        const val COLUMN_RECIPE_ID = "ps_jt_recipe_id"
        const val COLUMN_PREPARATION_STEP_ID = "ps_jt_preparation_step_id"
    }

    object MealPlanRecipesJoinTable {
        const val TABLE_NAME = "jt_meal_plan_recipes"
        const val COLUMN_MEAL_PLAN_ID = "mp_jt_meal_plan_id"
        const val COLUMN_RECIPE_ID = "mp_jt_recipe_id"
        const val COLUMN_MEAL_TYPE_ID = "mp_jt_meal_type_id"
    }

    object RecipeIngredientsIngredientMeasuresJoinTable {
        const val TABLE_NAME = "jt_recipe_ingredients_ingredient_measures"
        const val COLUMN_RECIPE_INGREDIENT_ID = "rim_jt_recipe_ingredient_id"
        const val COLUMN_INGREDIENT_MEASURE_ID = "rim_jt_ingredient_measure_id"
    }

}