package ksu.katara.healthymealplanner.mvvm.data.dietTips

import androidx.room.Dao
import androidx.room.Query
import ksu.katara.healthymealplanner.mvvm.data.dietTips.entities.DietTipChapterDBEntity
import ksu.katara.healthymealplanner.mvvm.data.dietTips.entities.DietTipDBEntity
import ksu.katara.healthymealplanner.mvvm.data.dietTips.entities.DietTipDetailDBEntity
import ksu.katara.healthymealplanner.mvvm.data.dietTips.entities.DietTipDetailStepDBEntity
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.DietTipChaptersTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.DietTipDetailStepsTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.DietTipDetailsTable
import ksu.katara.healthymealplanner.mvvm.data.room.DatabaseContract.DietTipsTable

@Dao
interface DietTipsDao {

    @Query("SELECT * FROM ${DietTipChaptersTable.TABLE_NAME} ")
    fun findChapters(): List<DietTipChapterDBEntity>

    @Query("SELECT * FROM ${DietTipsTable.TABLE_NAME} WHERE ${DietTipsTable.COLUMN_CHAPTER_ID} = :id")
    fun findByChapterId(id: Long): List<DietTipDBEntity>

    @Query("SELECT * FROM ${DietTipDetailsTable.TABLE_NAME} WHERE ${DietTipDetailsTable.COLUMN_ID} = :id")
    fun findDietTipDetailsById(id: Long): DietTipDetailDBEntity

    @Query("SELECT * FROM ${DietTipDetailStepsTable.TABLE_NAME} WHERE ${DietTipDetailStepsTable.COLUMN_DIET_TIP_DETAILS_ID} = :id")
    fun findDietTipDetailStepsById(id: Long): List<DietTipDetailStepDBEntity>

}