package ksu.katara.healthymealplanner.mvvm.model

open class AppException : RuntimeException()

class DietTipsNotFoundException : AppException()

class IngredientsNotFoundException : AppException()

class ProductNotFoundException : AppException()

class RecipeCategoryNotFoundException : AppException()

class RecipeDetailsNotFoundException : AppException()

class RecipeNotFoundException : AppException()

class ShoppingListRecipeNotFoundException : AppException()


