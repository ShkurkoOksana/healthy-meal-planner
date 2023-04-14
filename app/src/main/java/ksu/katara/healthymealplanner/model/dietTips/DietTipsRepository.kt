package ksu.katara.healthymealplanner.model.dietTips

import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.tasks.Task

interface DietTipsRepository {

    fun loadDietTipsChapters(): Task<Unit>

    fun loadDietTips(): Task<Unit>

    fun getDietTipDetailsById(id: Long): Task<DietTipDetails>

    fun addDietTipsChaptersListener(listener: DietTipsChaptersListener)

    fun addDietTipsListener(listener: DietTipsListener)

    fun removeDietTipsChaptersListener(listener: DietTipsChaptersListener)

    fun removeDietTipsListener(listener: DietTipsListener)

}
