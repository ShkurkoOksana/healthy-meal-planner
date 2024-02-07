package ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTip

@Entity(
    tableName = "diet_tips",
    indices = [
        Index("name", unique = true),
        Index("diet_tip_details_id"),
        Index("chapter_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = DietTipDetailDBEntity::class,
            parentColumns = ["id"],
            childColumns = ["diet_tip_details_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DietTipChapterDBEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapter_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DietTipDBEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val photo: String,
    val name: String,
    @ColumnInfo(name = "diet_tip_details_id") val dietTipDetailsId: Long,
    @ColumnInfo(name = "chapter_id") val chapterId: Long
) {

    fun toDietTip(): DietTip = DietTip(
        id = id,
        photo = photo,
        name = name,
        dietTipDetailsId = dietTipDetailsId,
        chapterId = chapterId
    )

}