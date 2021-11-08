package io.github.tomgarden.lib.permission.demo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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
                .grantedAllPermissions {
                    val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //用来打开相机的Intent
                    if (takePhotoIntent.resolveActivity(packageManager) != null) { //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
                        startActivityForResult(takePhotoIntent, 2) //启动相机
                    }
                }
                .deniedAllPermissions { }
                .requestPermissionsResult { allPermissionList, grantedPermissions, deniedPermissions ->
                    val message = "requestPermissionsResult " +
                            "\n*** allPermissionList \n $allPermissionList" +
                            "\n*** grantedPermissions \n $grantedPermissions" +
                            "\n*** deniedPermissions \n $deniedPermissions"

                    Logger.e(message)
                }
                //.request(this)
                .requestSimple(this)
        }
    }
}