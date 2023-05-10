package ksu.katara.healthymealplanner.model.recipecategories.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Long,
    val photo: String,
    val name: String,
) : Parcelable