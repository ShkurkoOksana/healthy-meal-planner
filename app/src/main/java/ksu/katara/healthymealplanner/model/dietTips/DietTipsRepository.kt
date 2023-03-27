package ksu.katara.healthymealplanner.model.dietTips

import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip

interface DietTipsRepository {

    fun loadDietTips()

    fun getDietTips(): List<DietTip>

}
