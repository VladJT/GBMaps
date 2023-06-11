package jt.projects.gbmaps.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import jt.projects.gbmaps.utils.LOG_TAG

open class PermissionActivity : AppCompatActivity() {
    private val REQUEST_CODE = 999

    private lateinit var onSuccess: () -> Unit
    private lateinit var onFailed: () -> Unit
    private lateinit var permission: String

    fun checkPermission(
        permission: String,
        rDlgTitle: String,
        rDlgMessage: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit = { defaultFailedNotification() }
    ) {
        this.onSuccess = onSuccess
        this.onFailed = onFailed
        this.permission = permission

        val permResult =
            ContextCompat.checkSelfPermission(this, permission)
        if (permResult == PackageManager.PERMISSION_GRANTED) {
            onSuccess.invoke()
        } else if (shouldShowRequestPermissionRationale(permission)) {
            AlertDialog.Builder(this)
                .setTitle(rDlgTitle)
                .setMessage(rDlgMessage)
                .setPositiveButton("Предоставить доступ") { _, _ ->
                    permissionRequest(permission)
                }
                .setNegativeButton("Отказаться") { dialog, _ ->
                    dialog.dismiss()
                    onFailed.invoke()
                }
                .create()
                .show()
        } else {
            permissionRequest(permission)
        }
    }

    private fun defaultFailedNotification() {
        AlertDialog.Builder(this)
            .setTitle("Отказ")
            .setMessage("Без соответствующих разрешений выполнение невозможно")
            .setPositiveButton(android.R.string.yes, null)
            .setIcon(android.R.drawable.ic_menu_help)
            .show()
    }

    private fun permissionRequest(permission: String) {
        requestPermissions(arrayOf(permission), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            for (pIndex in permissions.indices) {
                if (permissions[pIndex] == permission) {
                    if (grantResults[pIndex] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(LOG_TAG, "Доступ получен")
                        onSuccess.invoke()
                        return
                    } else {
                        if (!shouldShowRequestPermissionRationale(permissions[pIndex])) {
                            showGoSettings()
                        }else {
                            onFailed.invoke()
                        }
                    }
                }

            }
        }
    }

    private fun showGoSettings() {
        AlertDialog.Builder(this)
            .setTitle("Требуется настройка")
            .setMessage("Необходимо выдать разрешения вручную")
            .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                openApplicationSettings()
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                onFailed.invoke()
            }
            .create()
            .show()
    }

    private fun openApplicationSettings() {
        // запуск окна настроек приложения
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${packageName}")
        )
        settingsLauncher.launch(appSettingsIntent)
    }

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { permissionRequest(permission) }
}