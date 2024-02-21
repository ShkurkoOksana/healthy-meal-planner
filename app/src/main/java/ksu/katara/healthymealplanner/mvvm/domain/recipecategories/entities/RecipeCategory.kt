package ksu.katara.healthymealplanner.mvvm.domain.recipecategories.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents recipe category data
 */
@Parcelize
data class RecipeCategory(
    val id: Long,
    val photo: String,
    val name: String,
) : Parcelable