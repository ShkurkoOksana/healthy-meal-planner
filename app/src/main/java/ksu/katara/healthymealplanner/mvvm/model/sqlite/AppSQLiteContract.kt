package ksu.katara.healthymealplanner.mvvm.model.sqlite

class AppSQLiteContract {

    object DietTipChaptersTable {
        const val TABLE_NAME = "diet_tip_chapters"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }

    object DietTipsTable {
        const val TABLE_NAME = "diet_tips"
        const val COLUMN_ID = "id"
        const val COLUMN_PHOTO = "photo"
        const val COLUMN_NAME = "name"
        const val COLUMN_DIET_TIP_DETAILS_ID = "diet_tip_details_id"
        const val COLUMN_CHAPTER_ID = "chapter_id"
    }

    object DietTipDetailsTable {
        const val TABLE_NAME = "diet_tip_details"
        const val COLUMN_ID = "id"
        const val COLUMN_BACKGROUND = "background"
    }

    object DietTipDetailStepsTable {
        const val TABLE_NAME = "diet_tip_detail_steps"
        const val COLUMN_ID = "id"
        const val COLUMN_INDEX_NUMBER = "index_number"
        const val COLUMN_TITLE_NAME = "title_name"
        const val COLUMN_TITLE_DESCRIPTION = "title_description"
        const val COLUMN_DIET_TIP_DETAILS_ID = "diet_tip_details_id"
    }

}