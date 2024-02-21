package ksu.katara.healthymealplanner.mvvm.data.dietTips.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ksu.katara.healthymealplanner.mvvm.domain.dietTips.entities.DietTip

@Entity(
    tableName = "diet_tips",
    indices = [
        Index("diet_tip_name", unique = true),
        Index("diet_tip_details_id"),
        Index("diet_tip_chapter_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = DietTipDetailDBEntity::class,
            parentColumns = ["details_id"],
            childColumns = ["diet_tip_details_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DietTipChapterDBEntity::class,
            parentColumns = ["chapter_id"],
            childColumns = ["diet_tip_chapter_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DietTipDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "diet_tip_id") val id: Long,
    @ColumnInfo(name = "diet_tip_photo") val photo: String,
    @ColumnInfo(name = "diet_tip_name") val name: String,
    @ColumnInfo(name = "diet_tip_details_id") val dietTipDetailsId: Long,
    @ColumnInfo(name = "diet_tip_chapter_id") val chapterId: Long
) {

    fun toDietTip(): DietTip = DietTip(
        id = id,
        photo = photo,
        name = name,
        dietTipDetailsId = dietTipDetailsId,
        chapterId = chapterId
    )

}