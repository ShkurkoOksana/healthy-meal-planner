package ksu.katara.healthymealplanner

import ksu.katara.healthymealplanner.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.model.dietTips.InMemoryDietTipsRepository

object Repositories {

    val dietTipsRepository: DietTipsRepository = InMemoryDietTipsRepository()

}
