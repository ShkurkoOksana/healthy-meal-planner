package ksu.katara.healthymealplanner.mvvm.model.dietTips.entities

/**
 * Represents diet tip chapter data
 */
data class DietTipsChapter(
    val id: Long,
    val name: String,
    val dietTipsList: List<DietTip>
)

/**
 * Represents diet tip data
 */
data class DietTip(
    val id: Long,
    val photo: String,
    val name: String,
)

/**
 * Represents diet tip details data
 */
data class DietTipDetails(
    val dietTip: DietTip,
    val background: List<String>,
    val titlesList: List<String>,
    val descriptionsList: List<String>,
)
