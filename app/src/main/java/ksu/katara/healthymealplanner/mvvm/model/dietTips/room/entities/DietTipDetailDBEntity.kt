package ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetails

@Entity(
    tableName = "diet_tip_details"
)
data class DietTipDetailDBEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val background: String
) {

    fun toDietTipDetails() = DietTipDetails(
        id = id,
        background = background
    )

}
