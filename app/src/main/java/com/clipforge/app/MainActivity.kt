package com.clipforge.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.clipforge.app.ui.AnalysisScreen
import com.clipforge.app.ui.Bg
import com.clipforge.app.ui.ClipForgeTheme
import com.clipforge.app.ui.EditorScreen
import com.clipforge.app.ui.HomeScreen
import com.clipforge.app.ui.ProjectsScreen
import com.clipforge.app.ui.ResultsScreen
import com.clipforge.app.ui.SourceScreen

@UnstableApi
class MainActivity : ComponentActivity() {
    /** Set when the user taps the "highlights are ready" notification. */
    private var openProject by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        openProject = intent?.getStringExtra(EXTRA_PROJECT)
        askBatteryExemptionOnce()
        setContent { ClipForgeTheme { App(openProject) { openProject = null } } }
    }

    /** One-time system prompt so phones that aggressively kill background apps leave ClipForge running. */
    private fun askBatteryExemptionOnce() {
        try {
            val prefs = getSharedPreferences("clipforge", MODE_PRIVATE)
            if (prefs.getBoolean("asked_battery", false)) return
            prefs.edit().putBoolean("asked_battery", true).apply()
            val pm = getSystemService(POWER_SERVICE) as android.os.PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                startActivity(
                    Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        .setData(android.net.Uri.parse("package:$packageName"))
                )
            }
        } catch (_: Exception) {
            // Not available on this phone: background work still runs through the notification service.
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openProject = intent.getStringExtra(EXTRA_PROJECT)
    }

    override fun onStart() {
        super.onStart()
        AnalysisManager.appVisible = true
    }

    override fun onStop() {
        AnalysisManager.appVisible = false
        super.onStop()
    }
}

@UnstableApi
@Composable
private fun App(openProject: String?, onOpened: () -> Unit) {
    val nav = rememberNavController()
    val vm: AppViewModel = viewModel()
    val idArg = listOf(navArgument("id") { type = NavType.StringType })

    LaunchedEffect(openProject) {
        if (openProject != null) {
            nav.navigate("results/$openProject") { popUpTo("home") }
            onOpened()
        }
    }

    // The default page change is a long 700 ms cross-fade of two full screens. A short one-sided fade keeps it snappy.
    Box(Modifier.fillMaxSize().background(Bg)) {
        NavHost(
            nav,
            startDestination = "home",
            enterTransition = { fadeIn(tween(120)) },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { fadeOut(tween(120)) }
        ) {
            composable("home") { HomeScreen(vm, nav) }
            composable("source") { SourceScreen(vm, nav) }
            composable("analysis/{id}", idArg) { AnalysisScreen(vm, nav, it.arguments?.getString("id").orEmpty()) }
            composable("results/{id}", idArg) { ResultsScreen(vm, nav, it.arguments?.getString("id").orEmpty()) }
            composable("projects") { ProjectsScreen(vm, nav) }
            composable("editor") { EditorScreen(vm, nav) }
        }
    }
}
