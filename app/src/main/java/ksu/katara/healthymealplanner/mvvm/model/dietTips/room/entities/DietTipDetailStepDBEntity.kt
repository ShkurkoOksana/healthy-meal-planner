package ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetailSteps

@Entity(
    tableName = "diet_tip_detail_steps",
    indices = [
        Index("detail_step_details_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = DietTipDetailDBEntity::class,
            parentColumns = ["details_id"],
            childColumns = ["detail_step_details_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DietTipDetailStepDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "detail_step_id") val id: Long,
    @ColumnInfo(name = "detail_step_index_number") val indexNumber: Int,
    @ColumnInfo(name = "detail_step_title_name") val titleName: String,
    @ColumnInfo(name = "detail_step_title_description") val titleDescription: String,
    @ColumnInfo(name = "detail_step_details_id") val dietTipDetailsId: Long
) {

    fun toDietTipDetailSteps(): DietTipDetailSteps = DietTipDetailSteps(
        id = id,
        indexNumber = indexNumber,
        title = titleName,
        description = titleDescription,
        dietTipDetailId = dietTipDetailsId
    )

}



