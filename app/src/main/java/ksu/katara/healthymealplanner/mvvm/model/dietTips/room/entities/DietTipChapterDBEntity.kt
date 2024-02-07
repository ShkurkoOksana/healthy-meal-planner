package ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipChapter

@Entity(
    tableName = "diet_tip_chapters",

)
data class DietTipChapterDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String
) {

    fun toDietTipChapter(): DietTipChapter = DietTipChapter(
        id = id,
        name = name
    )

}