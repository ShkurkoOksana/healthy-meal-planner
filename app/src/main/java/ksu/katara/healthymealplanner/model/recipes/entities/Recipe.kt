package ksu.katara.healthymealplanner.model.recipes.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Recipe(
    val id: Long,
    val photo: String,
    val name: String,
) : Parcelable
