package ksu.katara.healthymealplanner.mvvm.model.dietTips.entities

/**
 * Represents diet tip chapter data
 */
data class DietTipChapter(
    val id: Long,
    val name: String,
)

/**
 * Represents diet tip data
 */
data class DietTip(
    val id: Long,
    val photo: String,
    val name: String,
    val dietTipDetailsId: Long,
    val chapterId: Long
)

/**
 * Represents diet tip details data
 */
data class DietTipDetails(
    val id: Long,
    val background: String,
)

data class DietTipDetailSteps(
    val id: Long,
    val indexNumber: Int,
    val title: String,
    val description: String,
    val dietTipDetailId: Long
)
