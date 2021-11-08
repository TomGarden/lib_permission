package io.github.tomgarden.lib.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager

/**
 * describe : 将这个 空 Activity 封装和 Permission 封装起来,
 *            避免在开发者调用的时候必须将权限请求与自己的 Activity 进行绑定
 *
 * author : Create by tom , on 2020/9/7-10:09 PM
 * github : https://github.com/TomGarden
 */
class PermissionActivity : Activity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PermissionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //设置 本 Activity 为 透明
        window.addFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.attributes.alpha = 0f

        Permission.PERMISSION_INSTANCE?.request(this)

        Log.e("asdfasdf", "asdfasdf")

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        /*如果产生内存泄露的问题可以考虑将回调调用移动到 onDestroy 中触发*/
        Permission.PERMISSION_INSTANCE?.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        Permission.PERMISSION_INSTANCE = null
        finish()
    }
}
