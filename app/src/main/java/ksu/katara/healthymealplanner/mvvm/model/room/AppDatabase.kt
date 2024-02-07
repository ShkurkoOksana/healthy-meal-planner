package ksu.katara.healthymealplanner.mvvm.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.DietTipsDao
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities.DietTipChapterDBEntity
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities.DietTipDBEntity
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities.DietTipDetailDBEntity
import ksu.katara.healthymealplanner.mvvm.model.dietTips.room.entities.DietTipDetailStepDBEntity

@Database(
    version = 3,
    entities = [
        DietTipChapterDBEntity::class,
        DietTipDBEntity::class,
        DietTipDetailDBEntity::class,
        DietTipDetailStepDBEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getDietTipsDao(): DietTipsDao

}