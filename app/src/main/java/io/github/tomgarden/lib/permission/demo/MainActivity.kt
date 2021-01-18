package io.github.tomgarden.lib.permission.demo

import android.Manifest
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.github.tomgarden.lib.log.Logger
import io.github.tomgarden.lib.permission.Permission

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnRequestPermission).setOnClickListener {
            Permission(this)
                .permissions(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .secondRationale { permission, activity, allPermissionList, grantedRequestList, firstRequestList, secondRequestList ->
                    val message = "二次权限请求 \n secondRationale \n*** allpermission \n $allPermissionList " +
                            "\n*** grantedRequestList \n $grantedRequestList" +
                            "\n*** firstRequestList \n $firstRequestList " +
                            "\n*** firstRequestList \n $secondRequestList"
                    Logger.i(message)

                    AlertDialog.Builder(this)
                        .setMessage(message)
                        .setPositiveButton(
                            "continue"
                        ) { _, _ ->
                            permission.continueRequest(activity, allPermissionList.toTypedArray())
                        }
                        .setNegativeButton("cancel", null)
                        .create()
                        .show()
                }
                .thirdRationale { permission, activity, allPermissionList, grantedRequestList, firstRequestList, secondRequestList, thirdRequestList ->
                    val message = "三次权限请求 \n thirdRationale \n*** allpermission \n $allPermissionList " +
                            "\n*** grantedRequestList \n $grantedRequestList" +
                            "\n*** firstRequestList \n $firstRequestList " +
                            "\n*** secondRequestList \n $secondRequestList"
                    AlertDialog.Builder(this)
                        .setMessage(message)
                        .setPositiveButton(
                            "setting"
                        ) { _, _ ->
                            permission.goAppSetting(activity)
                        }
                        .setNegativeButton("cancel", null)
                        .create()
                        .show()
                }
                .grantedPartPermissions { }
                .deniedPartPermissions { }
                .grantedAllPermissions { }
                .deniedAllPermissions { }
                .requestPermissionsResult { allPermissionList, grantedPermissions, deniedPermissions ->
                    val message = "requestPermissionsResult " +
                            "\n*** allPermissionList \n $allPermissionList" +
                            "\n*** grantedPermissions \n $grantedPermissions" +
                            "\n*** deniedPermissions \n $deniedPermissions"

                    Logger.e(message)
                }
                .request(this)
                .requestSimple(this)
        }
    }
}