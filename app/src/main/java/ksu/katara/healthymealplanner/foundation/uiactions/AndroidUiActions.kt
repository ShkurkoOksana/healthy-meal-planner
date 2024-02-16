package ksu.katara.healthymealplanner.foundation.uiactions

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android implementation of [UiActions]. Displaying simple toast message and getting string from resources.
 */
@Singleton
class AndroidUiActions @Inject constructor(
    @ApplicationContext private val appContext: Context
) : UiActions {

    override fun toast(message: String) {
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun getString(messageRes: Int, vararg args: Any): String {
        return appContext.getString(messageRes, *args)
    }

}