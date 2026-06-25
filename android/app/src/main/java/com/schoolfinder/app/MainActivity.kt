package com.schoolfinder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.schoolfinder.app.ui.AppRoot
import com.schoolfinder.app.ui.theme.SchoolFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchoolFinderTheme {
                AppRoot()
            }
        }
    }
}
