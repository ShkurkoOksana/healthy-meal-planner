package ksu.katara.healthymealplanner.mvvm.data.dietTips.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ksu.katara.healthymealplanner.mvvm.domain.dietTips.entities.DietTipChapter

@Entity(
    tableName = "diet_tip_chapters",

    )
data class DietTipChapterDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "chapter_id") val id: Long,
    @ColumnInfo(name = "chapter_name") val name: String
) {

    fun toDietTipChapter(): DietTipChapter = DietTipChapter(
        id = id,
        name = name
    )

}