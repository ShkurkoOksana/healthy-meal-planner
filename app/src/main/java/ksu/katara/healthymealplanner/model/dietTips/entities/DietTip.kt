package ksu.katara.healthymealplanner.model.dietTips.entities

data class DietTipChapter(
    val id: Long,
    val name: String,
)

data class DietTip(
    val id: Long,
    val photo: String,
    val name: String,
    val chapter: DietTipChapter,
)

data class DietTipDetails(
    val dietTip: DietTip,
    val background: String,
    val titlesList: List<String>,
    val descriptionsList: List<String>,
)
