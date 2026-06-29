package com.schoolfinder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.schoolfinder.app.ui.LocalRepository
import com.schoolfinder.app.ui.LocalSession
import com.schoolfinder.app.ui.RootApp
import com.schoolfinder.app.ui.theme.SchoolFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as SchoolFinderApplication

        setContent {
            SchoolFinderTheme {
                CompositionLocalProvider(
                    LocalRepository provides app.repository,
                    LocalSession provides app.session
                ) {
                    RootApp()
                }
            }
        }
    }
}
