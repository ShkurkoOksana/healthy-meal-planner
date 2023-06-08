package ksu.katara.healthymealplanner.model.dietTips

import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.tasks.Task

interface DietTipsRepository {

    fun loadDietTipsChapters(): Task<Unit>

    fun loadDietTips(): Task<Unit>

    fun addDietTipsListener(listener: DietTipsListener)

    fun removeDietTipsListener(listener: DietTipsListener)

    fun loadDietTipDetails(id: Long): Task<DietTipDetails>

    fun addDietTipsChaptersListener(listener: DietTipsChaptersListener)

    fun removeDietTipsChaptersListener(listener: DietTipsChaptersListener)


}
