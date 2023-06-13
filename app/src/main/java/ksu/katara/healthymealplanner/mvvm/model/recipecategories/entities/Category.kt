package ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents recipe category data
 */
@Parcelize
data class Category(
    val id: Long,
    val photo: String,
    val name: String,
) : Parcelable