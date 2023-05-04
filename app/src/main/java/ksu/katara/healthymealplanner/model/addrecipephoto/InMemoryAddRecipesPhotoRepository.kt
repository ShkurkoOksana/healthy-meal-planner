package ksu.katara.healthymealplanner.model.addrecipephoto

import ksu.katara.healthymealplanner.exceptions.AddRecipePhotoNotFoundException
import ksu.katara.healthymealplanner.model.addrecipephoto.entities.AddRecipePhoto
import ksu.katara.healthymealplanner.model.addrecipephoto.entities.AddRecipesPhotoRepository
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task

typealias AddRecipesPhotoListener = (addRecipePhoto: List<AddRecipePhoto>) -> Unit

class InMemoryAddRecipesPhotoRepository(
    private val recipesRepository: RecipesRepository,
) : AddRecipesPhotoRepository {

    private lateinit var addRecipesPhoto: MutableList<AddRecipePhoto>
    private var addRecipesPhotoLoaded = false
    private val addRecipesPhotoListeners = mutableSetOf<AddRecipesPhotoListener>()

    private lateinit var allAddRecipesPhoto: MutableList<AddRecipePhoto>

    override fun loadAddRecipesPhoto(): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            allAddRecipesPhoto = recipesRepository.getRecipes().map {
                AddRecipePhoto(
                    id = it.id,
                    photo = it.photo,
                )
            }.toMutableList()

            addRecipesPhoto = mutableListOf()

            addRecipesPhotoLoaded = true

            notifyAddRecipesPhotoChanges()
        }

    override fun addAddRecipesPhotoListener(listener: AddRecipesPhotoListener) {
        addRecipesPhotoListeners.add(listener)
        if (addRecipesPhotoLoaded) {
            listener.invoke(addRecipesPhoto)
        }
    }

    override fun removeAddRecipesPhotoListener(listener: AddRecipesPhotoListener) {
        addRecipesPhotoListeners.remove(listener)
    }

    override fun addRecipesPhotoAddRecipe(id: Long): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            val addRecipePhoto = allAddRecipesPhoto.firstOrNull { it.id == id } ?: throw AddRecipePhotoNotFoundException()

            addRecipesPhoto.add(addRecipePhoto)

            notifyAddRecipesPhotoChanges()
        }

    override fun addRecipesPhotoDeleteRecipe(addRecipePhoto: AddRecipePhoto): Task<Unit> =
        SimpleTask {
            Thread.sleep(500)

            val indexToDelete = addRecipesPhoto.indexOfFirst { it == addRecipePhoto }
            if (indexToDelete != -1) {
                addRecipesPhoto.removeAt(indexToDelete)

                notifyAddRecipesPhotoChanges()
            }
        }

    private fun notifyAddRecipesPhotoChanges() {
        if (!addRecipesPhotoLoaded) return
        addRecipesPhotoListeners.forEach { it.invoke(addRecipesPhoto) }
    }
}