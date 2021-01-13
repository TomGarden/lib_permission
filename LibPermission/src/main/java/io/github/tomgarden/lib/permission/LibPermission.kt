package io.github.tomgarden.lib.permission

import android.app.Activity

/**
 * describe :
 *
 * author : Create by tom , on 2020/9/7-10:11 PM
 * github : https://github.com/TomGarden
 */
class LibPermission {

    private val permissionCall: PermissionCall = PermissionCall()
    private val allPermissionList: MutableList<String> = mutableListOf()

    fun permissions(vararg allPermissionAry: String): LibPermission {
        allPermissionList.clear()
        allPermissionList.addAll(allPermissionAry)
        return this
    }

    fun request() {

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
    ): LibPermission {
        permissionCall.requestPermissionsResult = requestPermissionsResult
        return this
    }

    /** 权限请求结果——之关心被授予的权限
     *
     * @param grantedPermissions 只授予了部分权限，这是授予的部分权限
     * */
    fun grantedPartPermissions(grantedPermissions: (grantedPermissions: MutableList<String>) -> Unit): LibPermission {
        permissionCall.grantedPermissions = grantedPermissions
        return this
    }

    /** 权限请求结果——之关心被拒绝的权限
     *
     * @param deniedPermissions 只拒绝了部分权限，这是被拒绝的权限
     * */
    fun deniedPartPermissions(deniedPermissions: (deniedPermissions: MutableList<String>) -> Unit): LibPermission {
        permissionCall.deniedPermissions = deniedPermissions
        return this
    }

    /** 全部权限请求结果
     *
     * @param grantedAllPermissions 所有权限都被授予了，这是授予的权限列表
     * */
    fun grantedAllPermissions(grantedAllPermissions: (allPermissionList: MutableList<String>) -> Unit): LibPermission {
        permissionCall.grantedAllPermissions = grantedAllPermissions
        return this
    }

    /** 全部权限请求结果
     *
     * @param 所有权限都被拒绝了，这是被拒绝的权限列表
     * */
    fun deniedAllPermissions(deniedAllPermissions: (MutableList<String>) -> Unit): LibPermission {
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
    ): LibPermission {
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
    ): LibPermission {
        permissionCall.thirdRationale = thirdRationale
        return this
    }
    //endregion

}