package com.example.bilexport.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.bilexport.ui.theme.BiliExportTheme
import com.example.bilexport.ui.theme.DarkBackground
import com.example.bilexport.ui.viewmodel.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val libraryViewModel: LibraryViewModel by viewModels {
        LibraryViewModelFactory(mainViewModel)
    }
    private val exportViewModel: ExportViewModel by viewModels {
        ExportViewModelFactory(mainViewModel)
    }
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(mainViewModel)
    }

    // SAF 目录选择器
    private val pickDirLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            // 获取持久化权限
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            // 尝试将 SAF URI 转换为文件系统路径
            val fsPath = uriToFilePath(uri) ?: uri.toString()
            settingsViewModel.setExportDir(fsPath)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiliExportTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    AppNavigation(
                        mainViewModel = mainViewModel,
                        libraryViewModel = libraryViewModel,
                        exportViewModel = exportViewModel,
                        settingsViewModel = settingsViewModel,
                        onRequestPickDir = { pickDirLauncher.launch(null) }
                    )
                }
            }
        }
    }

    /**
     * 将 SAF 树 URI 转换为文件系统路径。
     * 适用于 content://com.android.externalstorage.documents/tree/primary%3AMovies%2FBiliExport
     */
    private fun uriToFilePath(treeUri: Uri): String? {
        val docId = try {
            DocumentsContract.getTreeDocumentId(treeUri)
        } catch (e: Exception) {
            return null
        }
        // docId 格式: "primary:Movies/BiliExport"
        val parts = docId.split(":")
        if (parts.size >= 2) {
            val path = parts.drop(1).joinToString(":")
            return "/storage/emulated/0/$path"
        }
        return null
    }
}

class LibraryViewModelFactory(
    private val mainViewModel: MainViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(
            mediaRepository = mainViewModel.let {
                com.example.bilexport.data.db.AppDatabase.getInstance(
                    mainViewModel.getApplication()
                ).let { db ->
                    com.example.bilexport.data.repository.MediaRepositoryImpl(db)
                }
            },
            scanLibraryUseCase = mainViewModel.scanLibraryUseCase,
            refreshLibraryUseCase = mainViewModel.refreshLibraryUseCase,
            searchQuery = mainViewModel.searchQuery,
            exportStateFilter = mainViewModel.exportStateFilter
        ) as T
    }
}

class ExportViewModelFactory(
    private val mainViewModel: MainViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = com.example.bilexport.data.db.AppDatabase.getInstance(
            mainViewModel.getApplication()
        )
        return ExportViewModel(
            exportRepository = com.example.bilexport.data.repository.ExportRepositoryImpl(db),
            mediaRepository = com.example.bilexport.data.repository.MediaRepositoryImpl(db),
            settingsRepository = com.example.bilexport.data.repository.SettingsRepositoryImpl(
                mainViewModel.getApplication()
            ),
            buildExportPreviewUseCase = mainViewModel.buildExportPreviewUseCase,
            createExportJobUseCase = mainViewModel.createExportJobUseCase,
            runExportJobUseCase = mainViewModel.runExportJobUseCase,
            retryExportJobUseCase = mainViewModel.retryExportJobUseCase,
            cleanupJobUseCase = mainViewModel.cleanupJobUseCase
        ) as T
    }
}

class SettingsViewModelFactory(
    private val mainViewModel: MainViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = com.example.bilexport.data.db.AppDatabase.getInstance(
            mainViewModel.getApplication()
        )
        return SettingsViewModel(
            settingsRepository = com.example.bilexport.data.repository.SettingsRepositoryImpl(
                mainViewModel.getApplication()
            ),
            mediaRepository = com.example.bilexport.data.repository.MediaRepositoryImpl(db),
            coverCache = mainViewModel.coverCache
        ) as T
    }
}
