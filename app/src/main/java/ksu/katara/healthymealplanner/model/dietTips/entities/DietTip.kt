package ksu.katara.healthymealplanner.model.dietTips.entities

data class DietTipsChapter(
    val id: Long,
    val name: String,
    val dietTipsList: List<DietTip>
)

data class DietTip(
    val id: Long,
    val photo: String,
    val name: String,
)

data class DietTipDetails(
    val dietTip: DietTip,
    val background: List<String>,
    val titlesList: List<String>,
    val descriptionsList: List<String>,
)
