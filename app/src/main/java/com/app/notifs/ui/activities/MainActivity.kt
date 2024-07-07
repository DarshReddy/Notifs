package com.app.notifs.ui.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.app.notifs.R
import com.app.notifs.helpers.printAsString
import com.app.notifs.ui.theme.NotifsTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NotifsTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    RequestNotificationPermissionDialog()
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        text = intent.extras?.printAsString() ?: "Empty Intent Data"
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun RequestNotificationPermissionDialog() {
        val shouldShowRationaleDialog = remember { mutableStateOf(true) }
        val shouldShowPermissionDialog = remember { mutableStateOf(true) }

        val permissionState =
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

        if (!permissionState.status.isGranted) {
            if (permissionState.status.shouldShowRationale) {
                RationaleDialog(shouldShowRationaleDialog)
            } else {
                PermissionDialog(shouldShowPermissionDialog) {
                    permissionState.launchPermissionRequest()
                }
            }
        }
    }

    @Composable
    fun RationaleDialog(shouldShowRationaleDialog: MutableState<Boolean>) {
        if (shouldShowRationaleDialog.value)
            AlertDialog(
                onDismissRequest = { shouldShowRationaleDialog.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val intent = Intent()
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.setData(uri)
                            startActivity(intent)
                        },
                    ) { Text(text = stringResource(R.string.open_settings)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { shouldShowRationaleDialog.value = false },
                    ) {
                        Text(text = stringResource(R.string.dismiss))
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.please_provide_this_permission)
                    )
                },
            )
    }

    @Composable
    fun PermissionDialog(
        shouldShowPermissionDialog: MutableState<Boolean>,
        onPermissionGranted: () -> Unit
    ) {
        if (shouldShowPermissionDialog.value)
            AlertDialog(
                onDismissRequest = { shouldShowPermissionDialog.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onPermissionGranted()
                        },
                    ) { Text(text = stringResource(R.string.provide_permission)) }
                },
                dismissButton = {
                    TextButton(onClick = { shouldShowPermissionDialog.value = false }) {
                        Text(text = stringResource(R.string.dismiss))
                    }
                },
                title = { Text(stringResource(R.string.please_provide_this_permission)) },
            )
    }
}