package ksu.katara.healthymealplanner.model.recipes

import ksu.katara.healthymealplanner.exceptions.IngredientsNotFoundException
import ksu.katara.healthymealplanner.exceptions.RecipeNotFoundException
import ksu.katara.healthymealplanner.model.product.ProductsRepository
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.model.recipes.entities.RecipePreparationStep
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task
import java.util.concurrent.Callable

typealias RecipeIngredientsListener = (recipeIngredients: List<RecipeIngredient>) -> Unit
typealias RecipesInCategoryListener = (recipes: List<Recipe>) -> Unit

class InMemoryRecipesRepository(
    private val productRepository: ProductsRepository,
) : RecipesRepository {

    private var recipes = mutableListOf<Recipe>()
    private var recipesDetails: MutableList<RecipeDetails>

    private var recipeTypes = listOf<String>()
    private var recipeTypesLoaded = false

    private var recipesInCategory = mutableListOf<Recipe>()
    private var recipeInCategoryLoaded = false
    private val recipesInCategoryListeners = mutableSetOf<RecipesInCategoryListener>()

    private var recipeIngredients = mutableListOf<RecipeIngredient>()
    private var recipeIngredientsLoaded = false
    private val recipeIngredientsListeners = mutableSetOf<RecipeIngredientsListener>()

    private var preparationSteps = mutableListOf<RecipePreparationStep>()
    private var preparationStepsLoaded = false

    private val recipesSize = PHOTOES.size

    private var isAllIngredientsSelected = false

    init {
        recipes = (0 until recipesSize).map {
            val recipeName = NAMES[it]
            Recipe(
                id = it.toLong(),
                name = recipeName,
                photo = PHOTOES[it],
                categoryId = CATEGORY.getValue(recipeName).toLong()
            )
        }.toMutableList()

        recipesDetails = (0 until recipesSize).map {
            val recipeName = recipes[it].name

            RecipeDetails(
                recipe = recipes[it],
                preparationTime = PREPARATION_TIME.getValue(recipeName),
                cuisineType = CUISINE_TYPES.getValue(recipeName),
                types = TYPES_LIST.getValue(recipeName),
                energeticValue = ENERGETIC_VALUE.getValue(recipeName),
                proteins = PROTEINS.getValue(recipeName),
                fats = FATS.getValue(recipeName),
                carbohydrates = CARBOHYDRATES.getValue(recipeName),
                ingredients = getIngredients(recipeName),
                preparationSteps = getPreparationSteps(recipeName),
                isFavorite = false,
                isAllIngredientsInShoppingList = false,
            )
        }.toMutableList()
    }

    private fun getIngredients(recipeName: String): MutableList<RecipeIngredient> {
        val recipeIngredients = mutableListOf<RecipeIngredient>()
        var ingredientsIndex = 0

        INGREDIENTS.getValue(recipeName).forEach { (id, measure) ->
            val product = productRepository.getProductById(id.toLong())
            val recipeIngredient = RecipeIngredient(
                id = ingredientsIndex.toLong(),
                product = product,
                amount = measure[0] as Double,
                measure = measure[1] as String,
                isInShoppingList = false,
            )

            recipeIngredients.add(recipeIngredient)
            ingredientsIndex++
        }

        return recipeIngredients
    }

    private fun getPreparationSteps(recipeName: String): MutableList<RecipePreparationStep> {
        val preparationSteps = mutableListOf<RecipePreparationStep>()
        var preparationStepsIndex = 0

        PREPARATION_STEPS.getValue(recipeName).forEach { (_, preparationStepsList) ->
            val preparationStep = RecipePreparationStep(
                id = preparationStepsIndex.toLong(),
                step = preparationStepsIndex + 1,
                photo = preparationStepsList[0],
                description = preparationStepsList[1]
            )

            preparationSteps.add(preparationStep)
            preparationStepsIndex++
        }

        return preparationSteps
    }

    override fun getRecipes(): MutableList<Recipe> = recipes

    override fun getRecipesDetails(): MutableList<RecipeDetails> = recipesDetails

    override fun getRecipeDetailsById(recipeId: Long): Task<RecipeDetails> = SimpleTask {
        Thread.sleep(200L)

        return@SimpleTask recipesDetails.firstOrNull<RecipeDetails> { it.recipe.id == recipeId } ?: throw IngredientsNotFoundException()
    }

    override fun loadRecipesInCategory(recipeCategoryId: Long): Task<Unit> =
        SimpleTask {
            Thread.sleep(200L)

            recipesInCategory = recipes.filter { it.categoryId == recipeCategoryId }.toMutableList()

            recipeInCategoryLoaded = true
            notifyRecipeInCategoryChanges()
        }

    override fun getRecipeInCategoryById(id: Long): Task<RecipeDetails> =
        SimpleTask(Callable {
            Thread.sleep(200L)

            val recipeInCategory = recipesInCategory.firstOrNull { it.id == id }

            return@Callable recipesDetails.firstOrNull<RecipeDetails> { it.recipe == recipeInCategory }
                ?: throw RecipeNotFoundException()
        })

    override fun addRecipeInCategoryListener(listener: RecipesInCategoryListener) {
        recipesInCategoryListeners.add(listener)
        if (recipeInCategoryLoaded) {
            listener.invoke(recipesInCategory)
        }
    }

    override fun removeRecipeInCategoryListener(listener: RecipesInCategoryListener) {
        recipesInCategoryListeners.remove(listener)
    }

    private fun notifyRecipeInCategoryChanges() {
        if (!recipeInCategoryLoaded) return
        recipesInCategoryListeners.forEach { it.invoke(recipesInCategory) }
    }

    override fun loadRecipeTypes(recipeId: Long): Task<List<String>> =
        SimpleTask {
            Thread.sleep(200L)

            val recipeDetails = recipesDetails.firstOrNull { it.recipe.id == recipeId } ?: throw IngredientsNotFoundException()

            recipeTypes = recipeDetails.types

            recipeTypesLoaded = true

            return@SimpleTask recipeTypes
        }

    override fun loadIngredients(recipeId: Long): Task<List<RecipeIngredient>> =
        SimpleTask(Callable {
            Thread.sleep(200L)

            val recipeDetails = recipesDetails.firstOrNull { it.recipe.id == recipeId } ?: throw IngredientsNotFoundException()

            recipeIngredients = recipeDetails.ingredients

            recipeIngredientsLoaded = true
            notifyIngredientsChanges()

            isAllIngredientsSelected = isAllIngredientsSelectedResult(recipeId)

            return@Callable recipeIngredients
        })

    private fun isAllIngredientsSelectedResult(id: Long): Boolean {
        val recipeDetails = recipesDetails.firstOrNull { it.recipe.id == id } ?: throw IngredientsNotFoundException()

        var countRecipeIngredientsSelected = 0
        recipeDetails.ingredients.forEach { recipeIngredient ->
            if (recipeIngredient.isInShoppingList) {
                countRecipeIngredientsSelected++
            }
        }

        return countRecipeIngredientsSelected == recipeIngredients.size && recipeIngredients.size != 0
    }

    override fun addIngredientListener(listener: RecipeIngredientsListener) {
        recipeIngredientsListeners.add(listener)
        if (recipeIngredientsLoaded) {
            listener.invoke(recipeIngredients)
        }
    }

    override fun removeIngredientsListener(listener: RecipeIngredientsListener) {
        recipeIngredientsListeners.remove(listener)
    }

    private fun notifyIngredientsChanges() {
        if (!recipeIngredientsLoaded) return
        recipeIngredientsListeners.forEach { it.invoke(recipeIngredients) }
    }

    override fun setIngredientSelected(recipeId: Long, ingredient: RecipeIngredient, isSelected: Boolean): Task<Boolean> = SimpleTask {
        Thread.sleep(200L)

        val recipeDetails = recipesDetails.firstOrNull { it.recipe.id == recipeId } ?: throw RecipeNotFoundException()

        val recipeIngredient = recipeDetails.ingredients.firstOrNull { it == ingredient } ?: throw IngredientsNotFoundException()
        recipeIngredient.isInShoppingList = isSelected

        isAllIngredientsSelected = isAllIngredientsSelectedResult(recipeId)

        recipeDetails.isAllIngredientsInShoppingList = isAllIngredientsSelected

        return@SimpleTask isAllIngredientsSelected
    }

    override fun setAllIngredientsSelected(recipeId: Long, isSelected: Boolean): Task<Unit> = SimpleTask {
        Thread.sleep(200L)

        val recipeDetails = recipesDetails.firstOrNull { it.recipe.id == recipeId } ?: throw RecipeNotFoundException()
        recipeDetails.ingredients.forEach { it.isInShoppingList = isSelected }

        recipeDetails.isAllIngredientsInShoppingList = isSelected
        isAllIngredientsSelected = isSelected
    }

    override fun isAllIngredientsSelected(recipeId: Long) = SimpleTask(
        Callable {
            Thread.sleep(200L)

            val recipeDetails = recipesDetails.firstOrNull { it.recipe.id == recipeId } ?: throw RecipeNotFoundException()

            return@Callable recipeDetails.isAllIngredientsInShoppingList
        }
    )

    override fun loadPreparationSteps(recipeId: Long): Task<MutableList<RecipePreparationStep>> =
        SimpleTask(Callable {
            Thread.sleep(200L)

            val recipeDetails = recipesDetails.firstOrNull { it.recipe.id == recipeId } ?: throw IngredientsNotFoundException()

            preparationSteps = recipeDetails.preparationSteps

            preparationStepsLoaded = true

            return@Callable preparationSteps
        })

    companion object {
        private val PHOTOES = listOf(
            "https://images.unsplash.com/photo-1612487439139-c2d7bac13577?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
            "https://images.unsplash.com/photo-1625944230945-1b7dd3b949ab?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1760&q=80",
            "https://images.unsplash.com/photo-1527976746453-f363eac4d889?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3432&q=80",
            "https://images.unsplash.com/photo-1551248429-40975aa4de74?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1980&q=80",
            "https://images.unsplash.com/photo-1485921325833-c519f76c4927?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1364&q=80",
        )

        private val NAMES = listOf(
            "Глазунья",
            "Греческий салат",
            "Борщ",
            "Салат цезарь с креветками",
            "Минтай тушенный",
        )

        private val CATEGORY = mapOf(
            "Глазунья" to 1,
            "Греческий салат" to 2,
            "Борщ" to 0,
            "Салат цезарь с креветками" to 2,
            "Минтай тушенный" to 1,
        )

        private val PREPARATION_TIME = mapOf(
            "Глазунья" to 15,
            "Греческий салат" to 15,
            "Борщ" to 120,
            "Салат цезарь с креветками" to 20,
            "Минтай тушенный" to 20,
        )

        private val CUISINE_TYPES = mapOf(
            "Глазунья" to "Русская кухня",
            "Греческий салат" to "Европейкая кухня",
            "Борщ" to "Украинская кухня",
            "Салат цезарь с креветками" to "Американская кухня",
            "Минтай тушенный" to "Русская кухня",
        )

        private val ENERGETIC_VALUE = mapOf(
            "Глазунья" to 199,
            "Греческий салат" to 117,
            "Борщ" to 51,
            "Салат цезарь с креветками" to 183,
            "Минтай тушенный" to 117,
        )

        private val PROTEINS = mapOf(
            "Глазунья" to 3,
            "Греческий салат" to 3,
            "Борщ" to 3,
            "Салат цезарь с креветками" to 9,
            "Минтай тушенный" to 12,
        )

        private val FATS = mapOf(
            "Глазунья" to 13,
            "Греческий салат" to 10,
            "Борщ" to 3,
            "Салат цезарь с креветками" to 12,
            "Минтай тушенный" to 7,
        )

        private val CARBOHYDRATES = mapOf(
            "Глазунья" to 10,
            "Греческий салат" to 4,
            "Борщ" to 4,
            "Салат цезарь с креветками" to 8,
            "Минтай тушенный" to 2,
        )

        private val TYPES_LIST = mapOf(
            "Глазунья" to listOf(
                "Диетический",
                "Для детей",
                "Завтрак",
                "Блюдо из яиц"
            ),
            "Греческий салат" to listOf(
                "Салат",
                "Обед",
                "Диетический",
            ),
            "Борщ" to listOf(
                "Суп",
                "Обед",
            ),
            "Салат цезарь с креветками" to listOf(
                "Ужин",
                "Морепродукты",
                "Салат",
                "Обед",
                "Завтрак"
            ),
            "Минтай тушенный" to listOf(
                "Рыба",
                "Ужин",
                "Диетический",
            ),
        )

        private val INGREDIENTS = mapOf(
            "Глазунья" to mapOf(
                0 to listOf(
                    1.0,
                    "шт.",
                ),
                13 to listOf(
                    0.0,
                    "по вкусу",
                ),
                1 to listOf(
                    8.3,
                    "гр.",
                ),
            ),
            "Греческий салат" to mapOf(
                7 to listOf(
                    20.0,
                    "гр.",
                ),
                6 to listOf(
                    0.2,
                    "шт.",
                ),
                4 to listOf(
                    0.1,
                    "шт.",
                ),
                5 to listOf(
                    0.6,
                    "шт.",
                ),
                9 to listOf(
                    3.0,
                    "шт.",
                ),
                2 to listOf(
                    14.0,
                    "гр.",
                ),
                11 to listOf(
                    0.6,
                    "ст.ложки",
                ),
                12 to listOf(
                    0.0,
                    "по вкусу",
                ),
                10 to listOf(
                    0.1,
                    "шт.",
                ),
                13 to listOf(
                    0.0,
                    "по вкусу",
                ),
                14 to listOf(
                    0.0,
                    "по вкусу",
                ),
            ),
            "Борщ" to mapOf(
                23 to listOf(
                    100.0,
                    "гр.",
                ),
                27 to listOf(
                    33.0,
                    "гр.",
                ),
                28 to listOf(
                    0.5,
                    "шт.",
                ),
                8 to listOf(
                    0.2,
                    "шт.",
                ),
                25 to listOf(
                    0.2,
                    "шт.",
                ),
                5 to listOf(
                    0.3,
                    "шт.",
                ),
                24 to listOf(
                    0.2,
                    "шт.",
                ),
                26 to listOf(
                    0.3,
                    "ст.ложки",
                ),
                22 to listOf(
                    0.4,
                    "л.",
                ),
                29 to listOf(
                    0.2,
                    "шт.",
                ),
                13 to listOf(
                    0.0,
                    "по вкусу",
                ),
                14 to listOf(
                    0.0,
                    "по вкусу",
                ),
            ),
            "Салат цезарь с креветками" to mapOf(
                15 to listOf(
                    200.0,
                    "гр.",
                ),
                16 to listOf(
                    150.0,
                    "гр.",
                ),
                17 to listOf(
                    3.0,
                    "шт.",
                ),
                0 to listOf(
                    1.0,
                    "шт.",
                ),
                18 to listOf(
                    1.5,
                    "зуб.",
                ),
                10 to listOf(
                    0.5,
                    "шт.",
                ),
                19 to listOf(
                    100.0,
                    "гр.",
                ),
                11 to listOf(
                    3.0,
                    "ст.ложки",
                ),
                20 to listOf(
                    0.5,
                    "ч.ложка",
                ),
                21 to listOf(
                    0.5,
                    "ч.ложка",
                ),
                13 to listOf(
                    0.0,
                    "по вкусу",
                ),
                14 to listOf(
                    0.0,
                    "по вкусу",
                ),
            ),
            "Минтай тушенный" to mapOf(
                32 to listOf(
                    300.0,
                    "гр.",
                ),
                25 to listOf(
                    0.15,
                    "шт.",
                ),
                8 to listOf(
                    0.5,
                    "шт.",
                ),
                1 to listOf(
                    25.0,
                    "гр.",
                ),
                13 to listOf(
                    0.0,
                    "по вкусу",
                ),
                14 to listOf(
                    0.0,
                    "по вкусу",
                ),
            ),
        )

        private val PREPARATION_STEPS = mapOf(
            "Глазунья" to mapOf(
                1 to listOf(
                    "https://images.unsplash.com/photo-1498654077810-12c21d4d6dc3?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                    "Яйца берите крупные отборные. Чтобы яичница получилась не только вкусной, " +
                            "но и красивой, лучше используйте деревенские яйца с крупным желтком" +
                            " насыщенного желтого цвета. Яйца обязательно берите свежие. Перед началом приготовления " +
                            "их необходимо помыть",
                ),
                2 to listOf(
                    "https://images.unsplash.com/photo-1589714379796-37d4bc8655c0?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2052&q=80",
                    "Поставьте сковороду на медленный огонь (у меня это режим 2 из 9) и хорошо прогрейте. " +
                            "На это может потребоваться до 5-7 минут. Выложите кусочек сливочного масла и " +
                            "растопите. Когда масло начнет пузыриться, аккуратно и не торопясь разбейте в " +
                            "сковороду яйца, стараясь сохранить желток целым. Очень важно прогреть сковороду" +
                            " заранее, чтобы белок при соприкосновении с горячей поверхностью сразу схватился" +
                            " и не растекся по всей сковороде тонким слоем.",
                ),
                3 to listOf(
                    "https://images.unsplash.com/photo-1620800981162-eb559427ad08?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                    "Жарьте яичницу на медленном огне без крышки около 5 минут. Белок должен схватиться, " +
                            "а желток остаться полужидким и ярким. Чтобы желток не затянулся белесой пленкой " +
                            "и сохранил полужидкую консистенцию, сковороду лучше не накрывайте крышкой. А если " +
                            "и накрываете, то не дольше 1-2 минут. При этом огонь нельзя добавлять, иначе яичница" +
                            " быстро поджарится и желток затвердеет.",
                ),
                4 to listOf(
                    "https://images.unsplash.com/photo-1620800981162-eb559427ad08?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                    "Переложите готовую яичницу на блюдо, украсьте половинками помидоров черри, ломтиками хлеба" +
                            " и веточками зелени. Приятного аппетита!.",
                ),
            ),
            "Греческий салат" to mapOf(
                1 to listOf(
                    "https://images.unsplash.com/photo-1615802546478-7380efa2d365?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                    "Подготавливаем необходимые продукты и специи для приготовления греческого салата с" +
                            " насыщенным и неординарным вкусом.",
                ),
                2 to listOf(
                    "https://plus.unsplash.com/premium_photo-1668616815449-b61c3f4d4f44?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                    "Наливаем в емкость оливковое масло. Добавляем одну чайную ложку прованских трав и выжимаем сок лимона.",
                ),
                3 to listOf(
                    "https://images.unsplash.com/photo-1622205313162-be1d5712a43f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3004&q=80",
                    "Крошим кусочками хорошо промытые листья салата и выкладываем их в объемную миску" +
                            " для подачи салата.",
                ),
                4 to listOf(
                    "https://images.unsplash.com/photo-1589621316382-008455b857cd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                    "Нарезаем произвольным средним кубиком огурец. Равномерным слоем выкладываем нарезанный" +
                            " кубиком огурец сверху листьев салата",
                ),
                5 to listOf(
                    "https://images.unsplash.com/photo-1597226051193-7335b1796546?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                    "Нарезаем произвольным средним кубиком помидоры.",
                ),
                6 to listOf(
                    "https://images.unsplash.com/photo-1661349008073-136bed6e6788?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                    "Выкладываем и равномерно размещаем кусочки сыра в салате.",
                ),
                7 to listOf(
                    "https://images.unsplash.com/photo-1596099477998-880bc06e09f9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                    "Выкладываем в салат цельные маслины.",
                ),
                8 to listOf(
                    "https://images.unsplash.com/photo-1505253716362-afaea1d3d1af?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3648&q=80",
                    "Заправляем салат приготовленным и настоявшимся соусом. " +
                            "Добавляем в салат по вкусу смесь из черного с красным перцев.",
                ),
            ),
            "Борщ" to mapOf(
                1 to listOf(
                    "https://images.unsplash.com/photo-1635321593217-40050ad13c74?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3496&q=80",
                    "Как сварить борщ со свежей капустой, свеклой и мясом? Подготовьте для этого необходимые " +
                            "ингредиенты. Капусту берите свежую. Овощи хорошо помойте от загрязнений и очистите" +
                            " от кожуры.",
                ),
                2 to listOf(
                    "https://images.unsplash.com/photo-1604503468506-a8da13d82791?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                    "Свинину можно использовать любую часть. Но если варить свинину на кости, то бульон будет " +
                            "получаться более наваристый. Поместите свинину в холодную воду, доведите до кипения" +
                            " и варите 1-1,5 часа. При закипании снимайте пенку, которая будет появляться. " +
                            "Готовый бульон процедите. Мясо нарежьте на кусочки и верните в кастрюлю.",
                ),
                3 to listOf(
                    "https://images.unsplash.com/photo-1566320239257-2a4d2745d771?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                    "Капусту нашинкуйте, а картофель нарежьте на кусочки. " +
                            "Выложите капусту и картофель в кастрюлю с кипящим бульоном и оставьте вариться минут на 15.",
                ),
                4 to listOf(
                    "https://images.unsplash.com/photo-1567137827022-fbe18eff7275?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2274&q=80",
                    "В это время мелко нарежьте лук и натрите на терке морковь. Обжарьте лук и морковь в " +
                            "течение 4-5 минут на небольшом огне. Затем добавьте в зажарку нарезанные помидоры." +
                            " Обжаривайте все вместе еще 3-4 минуты. За это время помидоры пустят сок и " +
                            "станут мягкие. Готовую зажарку выложите в кастрюлю.",
                ),
                5 to listOf(
                    "https://images.unsplash.com/photo-1503623758111-863eb2abd7a9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                    "Отдельно обжарьте свеклу, натертую на терке. Добавьте к ней немного томатной пасты." +
                            " Она придаст борщу небольшую кислинку и насыщенный цвет. На жарку свеклы уйдет " +
                            "еще минут 5-7. За время жарки она немного уменьшится в размере.",
                ),
                6 to listOf(
                    "https://images.unsplash.com/photo-1526401363794-c96708fb8089?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                    "Выложите обжаренную свеклу в кастрюлю. Добавьте лавровый лист. По вкусу добавьте немного " +
                            "соли и специи. Перемешайте, доведите до кипения и снимите с огня. Кипятить со свеклой" +
                            " борщ не надо. От этого он потеряет цвет. Готовый борщ накройте крышкой и оставьте " +
                            "постоять минут на 15-20, чтобы он немного настоялся. Готовый борщ подавайте украсив " +
                            "зеленью и добавьте ложечку сметаны. Приятного аппетита!",
                ),
            ),
            "Салат цезарь с креветками" to mapOf(
                1 to listOf(
                    "https://images.unsplash.com/photo-1556386734-4227a180d19e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1305&q=80",
                    "Как сделать салат цезарь классический с креветками? Первым делом подготовьте все" +
                            " необходимые ингредиенты по списку. Креветки для салата лучше взять " +
                            "тигровые или королевские, но и с обычными получится не менее вкусно. " +
                            "Какие листы салата лучше использовать? В классическом рецепте используют " +
                            "Романо, можно взять Латук, Эндвий, Айсберг или Пекинскую капусту. " +
                            "Помидоры, лимон и листья ополосните в чистой воде, обсушите салфетками.",
                ),
                2 to listOf(
                    "https://images.unsplash.com/photo-1580317092099-ade9937dee4f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1321&q=80",
                    "Креветки выньте из морозильной камеры и разморозьте при комнатной температуре. " +
                            "Для более быстрой разморозки можно залить креветки прохладной водой. " +
                            "Удалите с моллюсков панцири и головы, хвостики можно оставить, либо тоже убрать- " +
                            "на ваше усмотрение. Далее существует два способа термической обработки креветок. " +
                            "Их можно отварить в кипящей воде до готовности или замариновать и обжарить в " +
                            "сковороде. Я чаще готовлю вторым способом, так вкус салата, как мне кажется, " +
                            "получается более насыщенным. Итак, в глубокую небольшую чашку отправьте креветки, " +
                            "добавьте 1ст.л. сока лимона, 2 ст.л. растительного или оливкового масла и щепотку " +
                            "соли. Если у вас есть вустерский соус, то добавьте его тоже. Оставьте на 10 мин.",
                ),
                3 to listOf(
                    "https://images.unsplash.com/photo-1587157987149-57586c595a6b?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                    "Обжарьте креветки на горячей сковороде пару минут с одной стороны. И ещё столько же с другой.",
                ),
                4 to listOf(
                    "https://images.unsplash.com/photo-1508616185939-efe767994166?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1335&q=80",
                    "С белого хлеба (можно использовать батон) срежьте корочки. Нарежьте хлеб небольшими кубиками " +
                            "размером 1-2 см. Обжарьте на небольшом количестве оливкового или растительного масла. " +
                            "При желании можно добавить немного чеснока, так сухарики получатся ароматными.",
                ),
                5 to listOf(
                    "https://images.unsplash.com/photo-1586276424667-06c0b45b90ba?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                    "Яйца залейте холодной водой и поставьте на плиту. После закипания пламя слегка убавьте и" +
                            " варите яйца одну минуту. Они должны получиться всмятку. Нам понадобятся только " +
                            "желтки для приготовления заправки.",
                ),
                6 to listOf(
                    "https://images.unsplash.com/photo-1611516081814-55d97d5a7488?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2376&q=80",
                    "Подготовьте соус. Для этого в чашу блендера поместите два желтка, горчицу, 2 ст.л. сока" +
                            " лимона, бальзамический уксус, 4 ст.л. оливкового или растительного масла, чеснок," +
                            " соль, сахар и чёрный молотый перец. Взбейте всё до однородной массы.",
                ),
                7 to listOf(
                    "https://images.unsplash.com/photo-1622205313162-be1d5712a43f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3004&q=80",
                    "Осталось собрать салат. Листы нарвите руками произвольно, отправьте на плоскую тарелку первым слоем.",
                ),
                8 to listOf(
                    "https://images.unsplash.com/photo-1551248429-40975aa4de74?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1890&q=80",
                    "Далее идёт очередь сухариков и томатов черри. Помидоры можно разрезать пополам или четвертинками. " +
                            "Выложите следующим слоем креветки. Равномерно полейте салат соусом. И добавьте сыр, " +
                            "натёртый на мелкой тёрке. По желанию сверху посыпьте сухариками и украсьте зеленью." +
                            " Цезарь готов, подавайте сразу же! Слегка перемешайте салат у себя в тарелке и " +
                            "насладитесь потрясающим вкусом!",
                ),
            ),
            "Минтай тушенный" to mapOf(
                1 to listOf(
                    "https://images.unsplash.com/photo-1510130387422-82bed34b37e9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1335&q=80",
                    "Как потушить минтай с луком и морковью? Подготовьте продукты по списку. " +
                            "Рыбу заранее разморозьте, можно не полностью, так ее будет легче разделывать. " +
                            "Морковь хорошо помойте щеткой под проточной водой, затем обсушите. Репчатый лук " +
                            "очистите от шелухи, помойте, обсушите. Рыбу очистите от чешуи, отрежьте голову, " +
                            "хвост, плавники. Хорошо промойте под струей холодной воды, обсушите бумажными " +
                            "полотенцами. Затем порежьте рыбу на порционные кусочки.",
                ),
                2 to listOf(
                    "https://plus.unsplash.com/premium_photo-1663047563264-20f61cc983a8?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                    "Минтай посолите и поперчите. На сковороде разогрейте растительное масло и быстро обжарьте " +
                            "минтая с двух сторон, не отходя от плиты. Будьте внимательны: если в горячее масло " +
                            "попадет капля воды, оно начнет шипеть и брызгаться. Поэтому важно, чтобы на рыбе " +
                            "воды не оставалось.",
                ),
                3 to listOf(
                    "https://images.unsplash.com/photo-1636065641381-a3bf345e9a02?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                    "Натрите на крупной терке морковь и добавьте к рыбе. Влейте в сковороду полстакана воды, " +
                            "тушите блюдо под крышкой минут 10. За это время и рыба, и овощи успеют приготовиться." +
                            " Перед выключением плиты проверьте их на готовность на всякий случай, потому что " +
                            "ваша плита может готовить быстрее или медленнее моей.",
                ),
                4 to listOf(
                    "https://images.unsplash.com/photo-1485921325833-c519f76c4927?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1364&q=80",
                    "Подавайте тушеный минтай с морковью и луком прямо на сковороде, присыпав свежей зеленью. " +
                            "Или можете разложить по тарелкам с гарниром или без гарнира, со свежим хлебом, " +
                            "который очень вкусно макать в образовавшийся при тушении рыбы с овощами соус.",
                ),
            ),
        )
    }
}