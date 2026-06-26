package com.example.bilexport.app

import android.net.Uri
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bilexport.ui.screen.*
import com.example.bilexport.ui.viewmodel.*

object Routes {
    const val LIBRARY = "library"
    const val DETAIL = "detail/{mediaItemId}"
    const val EXPORT_QUEUE = "export_queue"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val DIAGNOSTIC = "diagnostic"

    fun detailRoute(mediaItemId: Long) = "detail/$mediaItemId"
}

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    libraryViewModel: LibraryViewModel,
    exportViewModel: ExportViewModel,
    settingsViewModel: SettingsViewModel,
    onRequestPickDir: () -> Unit = {}
) {
    val navController = rememberNavController()
    val shizukuAvailable by mainViewModel.shizukuAvailable.collectAsState()
    val shizukuPermissionGranted by mainViewModel.shizukuPermissionGranted.collectAsState()
    var hasRedirected by remember { mutableStateOf(false) }

    // Shizuku 异常时自动跳转到诊断页
    LaunchedEffect(shizukuAvailable, shizukuPermissionGranted) {
        if (!hasRedirected && (!shizukuAvailable || !shizukuPermissionGranted)) {
            hasRedirected = true
            navController.navigate(Routes.DIAGNOSTIC) {
                popUpTo(Routes.LIBRARY) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LIBRARY
    ) {
        composable(Routes.LIBRARY) {
            LibraryScreen(
                mainViewModel = mainViewModel,
                libraryViewModel = libraryViewModel,
                exportViewModel = exportViewModel,
                onExport = { ids -> exportViewModel.batchExport(ids) },
                onNavigateToDetail = { mediaItemId ->
                    navController.navigate(Routes.detailRoute(mediaItemId))
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("mediaItemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val mediaItemId = backStackEntry.arguments?.getLong("mediaItemId") ?: return@composable
            val items by libraryViewModel.allItems.collectAsState()
            val item = items.find { it.id == mediaItemId }

            if (item != null) {
                DetailScreen(
                    mediaItem = item,
                    onExport = { mediaItem, types -> exportViewModel.singleExport(mediaItem, types) },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.EXPORT_QUEUE) {
            ExportQueueScreen(
                exportViewModel = exportViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onRequestPickDir = onRequestPickDir,
                onBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT) },
                onNavigateToDiagnostic = { navController.navigate(Routes.DIAGNOSTIC) }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.DIAGNOSTIC) {
            DiagnosticScreen(
                diagnosticRepository = mainViewModel.diagnosticRepository,
                shizukuAvailable = shizukuAvailable,
                shizukuPermissionGranted = shizukuPermissionGranted,
                onBack = { navController.popBackStack() },
                onNavigateToLibrary = {
                    navController.navigate(Routes.LIBRARY) {
                        popUpTo(Routes.DIAGNOSTIC) { inclusive = true }
                    }
                }
            )
        }
    }
}
