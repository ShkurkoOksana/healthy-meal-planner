package ksu.katara.healthymealplanner.model.dietTips

import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.tasks.Task

interface DietTipsRepository {

    fun loadDietTipsForHomeScreen(): Task<Unit>

    fun getById(id: Long): Task<DietTipDetails>

    fun addListener(listener: DietTipsListener)

    fun removeListener(listener: DietTipsListener)
}
