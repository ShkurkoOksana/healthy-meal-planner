package ksu.katara.healthymealplanner.model.addrecipephoto.entities

import ksu.katara.healthymealplanner.model.addrecipephoto.AddRecipesPhotoListener
import ksu.katara.healthymealplanner.tasks.Task

interface AddRecipesPhotoRepository {

    fun loadAddRecipesPhoto(): Task<Unit>

    fun addAddRecipesPhotoListener(listener: AddRecipesPhotoListener)

    fun removeAddRecipesPhotoListener(listener: AddRecipesPhotoListener)

    fun addRecipesPhotoAddRecipe(id: Long): Task<Unit>

    fun addRecipesPhotoDeleteRecipe(addRecipePhoto: AddRecipePhoto): Task<Unit>

}