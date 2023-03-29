package ksu.katara.healthymealplanner.model.dietTips

import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipDetails

interface DietTipsRepository {

    fun loadDietTips()

    fun getDietTips(): List<DietTip>

    fun getById(id: Long): DietTipDetails
}
