package io.github.tomgarden.lib.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * describe : 权限请求，逻辑特色是能够分辨权限请求的次数，能分辨当前权限是第几次请求，根据当前的请求次数可以做出恰当的响应
 * note : 如果权限首次请求被授予后有在设置中取消了对权限的授予应该如何,已测试，无异常
 *
 * <p>author : tom
 * <p>time : 19-8-3 10:22
 * <p>GitHub : https://github.com/TomGarden
 */

class Permission(context: Context, val permissionCall: PermissionCall) {

    private val TAG = "tom.work@foxmail.com"
    private val sharedPreferences by lazy {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }
    private val FILE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
    val REQUEST_CODE: Int = 0/*关于这个 code 如果有需求我们再做详究,当前设置此属性不应被过多使用，要用 「mRequestCode」*/
    private var mRequestCode: Int = REQUEST_CODE

    companion object {
        var PERMISSION_INSTANCE: Permission? = null
        fun clearSelf() {
            /**如果发起真正的权限请求在 onRequestPermissionsResult 清理自身
             * 如果不发起权限请求 , 在回调函数执行完毕后清理自身
             * */
            PERMISSION_INSTANCE = null
        }
    }

    constructor(context: Context) : this(context, PermissionCall())


    /*这三个列表在权限请求发起前会被明确区分出来*/
    private val allPermissionList: MutableList<String> = mutableListOf()
    private val grantedRequestList: MutableList<String> = mutableListOf()//权限列表中已经被授予的权限
    private val firstRequestList: MutableList<String> = mutableListOf()//首次请求权限列表
    private val secondRequestList: MutableList<String> = mutableListOf()//二次请求权限列表
    private val thirdRequestList: MutableList<String> = mutableListOf()//三次请求权限列表

    /**
     * @param allPermissionAry 设置本次调用需要请求的所有权限
     */
    fun permissions(vararg allPermissionAry: String): Permission {
        allPermissionList.clear()
        allPermissionList.addAll(allPermissionAry)
        return this
    }

    /**
     * 请求权限入口函数，从调用这个方法开始就开始了权限请求流程了
     *
     * 这种请求方式无需设置 [Activity.onRequestPermissionsResult]
     */
    fun request(activity: Activity, requestCode: Int = REQUEST_CODE): Permission {
        firstRequestList.clear()
        secondRequestList.clear()
        thirdRequestList.clear()
        grantedRequestList.clear()

        mRequestCode = requestCode

        /*
        * [requestFlag]值的意义
        *        「首次权限请求」权限列表中的所有权限都是第一次请求
        *        「二次权限请求」权限列表中的所有权限中包含二次请求的权限
        *        「三次权限请求」权限列表中的所有权限中包含三次请求的权限
        */
        var requestFlag: RequestFlag = RequestFlag.DEF

        val allPermissionAry = allPermissionList.toTypedArray()

        allPermissionAry.forEach { permission ->
            when (ContextCompat.checkSelfPermission(activity, permission)) {
                PackageManager.PERMISSION_DENIED -> {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                        /*二次请求权限*/
                        secondRequestList.add(permission)
                        if (requestFlag.flag < RequestFlag.SECOND.flag) {
                            requestFlag = RequestFlag.SECOND
                        }
                    } else if (sharedPreferences.getBoolean(permission, false)) {
                        /*三次请求权限*/
                        thirdRequestList.add(permission)
                        if (requestFlag.flag < RequestFlag.THIRD.flag) {
                            requestFlag = RequestFlag.THIRD
                        }
                    } else {
                        /*首次请求权限*/
                        firstRequestList.add(permission)
                        if (requestFlag.flag < RequestFlag.FIRST.flag) {
                            requestFlag = RequestFlag.FIRST
                        }
                    }
                }
                PackageManager.PERMISSION_GRANTED -> grantedRequestList.add(permission)
            }
        }

        when (requestFlag) {
            RequestFlag.FIRST -> {
                ActivityCompat.requestPermissions(activity, allPermissionAry, mRequestCode)
            }
            RequestFlag.SECOND -> {
                permissionCall.secondRationale?.invoke(
                    this,
                    activity,
                    allPermissionList,
                    grantedRequestList,
                    firstRequestList,
                    secondRequestList
                ) ?: let {
                    ActivityCompat.requestPermissions(activity, allPermissionAry, mRequestCode)
                }
            }
            RequestFlag.THIRD -> {
                permissionCall.thirdRationale?.invoke(
                    this,
                    activity,
                    allPermissionList,
                    grantedRequestList,
                    firstRequestList,
                    secondRequestList,
                    thirdRequestList
                )

                clearSelf()
            }
            RequestFlag.DEF -> {
                permissionCall.grantedAllPermissions?.invoke(allPermissionList)

                clearSelf()
            }
        }

        return this
    }

    /**
     * 发起权限请求
     *
     * 这种请求方式无需设置 [Activity.onRequestPermissionsResult]
     */
    fun requestSimple(context: Context) {
        PERMISSION_INSTANCE = this
        PermissionActivity.start(context)
    }

    /**
     * 请求结果回调
     */
    fun onRequestPermissionsResult(
        requestCode: Int, allPermissionAry: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode != mRequestCode) return

        val grantedList: MutableList<String> = mutableListOf()
        val deniedList: MutableList<String> = mutableListOf()

        grantResults.forEachIndexed { index, grantResult ->
            when (grantResult) {
                PackageManager.PERMISSION_DENIED -> {
                    deniedList.add(allPermissionAry[index])
                    localFlag(allPermissionAry[index])
                }
                PackageManager.PERMISSION_GRANTED -> grantedList.add(allPermissionAry[index])
            }
        }

        /*授予部分权限回调*/
        permissionCall.grantedPermissions?.invoke(grantedList)
        /*拒绝部分权限回调*/
        permissionCall.deniedPermissions?.invoke(deniedList)
        /*授予全部去权限回调*/
        if (grantedList.isNotEmpty() && deniedList.isEmpty()) {
            permissionCall.grantedAllPermissions?.invoke(allPermissionAry.toMutableList())
        }
        /*拒绝全部权限回调*/
        if (grantedList.isEmpty() && deniedList.isNotEmpty()) {
            permissionCall.deniedAllPermissions?.invoke(allPermissionAry.toMutableList())
        }
    }


    fun continueRequest(activity: Activity, permissions: Array<out String>) {
        ActivityCompat.requestPermissions(activity, permissions, mRequestCode)
    }

    fun goAppSetting(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }

    fun deniedAll() {
        permissionCall.deniedAllPermissions?.invoke(allPermissionList)
    }

    //region 设置各个回调接口
    /** 全部权限请求结果
     *
     *@param allPermissionList  所有请求的权限
     *@param grantedPermissions 请求后被授予的权限
     *@param deniedPermissions  请求后被拒绝的权限
     * */
    fun requestPermissionsResult(
        requestPermissionsResult: (
            allPermissionList: MutableList<String>,
            grantedPermissions: MutableList<String>,
            deniedPermissions: MutableList<String>
        ) -> Unit
    ): Permission {
        permissionCall.requestPermissionsResult = requestPermissionsResult
        return this
    }

    /** 权限请求结果——之关心被授予的权限
     *
     * @param grantedPermissions 只授予了部分权限，这是授予的部分权限
     * */
    fun grantedPartPermissions(grantedPermissions: (grantedPermissions: MutableList<String>) -> Unit): Permission {
        permissionCall.grantedPermissions = grantedPermissions
        return this
    }

    /** 权限请求结果——之关心被拒绝的权限
     *
     * @param deniedPermissions 只拒绝了部分权限，这是被拒绝的权限
     * */
    fun deniedPartPermissions(deniedPermissions: (deniedPermissions: MutableList<String>) -> Unit): Permission {
        permissionCall.deniedPermissions = deniedPermissions
        return this
    }

    /** 全部权限请求结果
     *
     * @param grantedAllPermissions 所有权限都被授予了，这是授予的权限列表
     * */
    fun grantedAllPermissions(grantedAllPermissions: (allPermissionList: MutableList<String>) -> Unit): Permission {
        permissionCall.grantedAllPermissions = grantedAllPermissions
        return this
    }

    /** 全部权限请求结果
     *
     * @param 所有权限都被拒绝了，这是被拒绝的权限列表
     * */
    fun deniedAllPermissions(deniedAllPermissions: (MutableList<String>) -> Unit): Permission {
        permissionCall.deniedAllPermissions = deniedAllPermissions
        return this
    }

    /** 针对二次权限请求作出的必要解释
     *
     * @param permission 本类自身
     * @param activity
     * @param allPermissionList     本次请求的所有权限
     * @param grantedRequestList    本次请求的所有权限中已经被授予的权限
     * @param firstRequestList      本次请求的所有权限中首次被请求的权限
     * @param secondRequestList     本次请求的所有权限中二次被请求的权限
     */
    fun secondRationale(
        secondRationale: (
            permission: Permission,
            activity: Activity,
            allPermissionList: MutableList<String>,
            grantedRequestList: MutableList<String>,
            firstRequestList: MutableList<String>,
            secondRequestList: MutableList<String>
        ) -> Unit
    ): Permission {
        permissionCall.secondRationale = secondRationale
        return this
    }

    /**针对三次权限请求作出的必要解释
     *
     * @param permission
     * @param activity
     * @param allPermissionList     本次请求的所有权限
     * @param grantedRequestList    本次请求的所有权限中已经被授予的权限
     * @param firstRequestList      本次请求的所有权限中首次被请求的权限
     * @param secondRequestList     本次请求的所有权限中二次被请求的权限
     * @param thirdRequestList      本次请求的所有权限中三次被请求的权限
     */
    fun thirdRationale(
        thirdRationale: (
            permission: Permission,
            activity: Activity,
            allPermissionList: MutableList<String>,
            grantedRequestList: MutableList<String>,
            firstRequestList: MutableList<String>,
            secondRequestList: MutableList<String>,
            thirdRequestList: MutableList<String>
        ) -> Unit
    ): Permission {
        permissionCall.thirdRationale = thirdRationale
        return this
    }
    //endregion

    //region 本地化工具方法
    private fun localFlag(permission: String) {
        sharedPreferences.edit()?.putBoolean(permission, true)?.apply()
    }
    //endregion 本地化工具方法

}