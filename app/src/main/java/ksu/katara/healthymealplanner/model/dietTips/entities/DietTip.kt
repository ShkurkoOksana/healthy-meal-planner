package ksu.katara.healthymealplanner.model.dietTips.entities

data class DietTip(
    val id: Long,
    val photo: String,
    val name: String,
)

data class DietTipDetails(
    val dietTip: DietTip,
    val descriptions: String
)