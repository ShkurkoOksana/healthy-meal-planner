package ksu.katara.healthymealplanner.mvvm.model.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppSQLiteHelper(
    private val applicationContext: Context
) : SQLiteOpenHelper(applicationContext, "healthy_meal_planner.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        executeSQLFromAsserts(db, "db_init.sql")
        executeSQLFromAsserts(db, "db_update_to_version_2.sql")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when {
            oldVersion == 1 && newVersion == 2 -> {
                executeSQLFromAsserts(db, "db_update_to_version_2.sql")
            }
        }
    }

    private fun executeSQLFromAsserts(db: SQLiteDatabase, fileName: String) {
        val sql = applicationContext.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
        sql.split(';')
            .filter { it.isNotBlank() }
            .forEach {
                db.execSQL(it)
            }
    }

}