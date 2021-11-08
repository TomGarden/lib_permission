package io.github.tomgarden.lib.permission

import android.app.Activity

/**
 * describe : 权限请求过程中可能会用到的回调们
 *
 * author : Create by tom , on 2020/9/7-10:13 PM
 * github : https://github.com/TomGarden
 */
class PermissionCall {


    /*授权结果*/
    var requestPermissionsResult: ((
        allPermissionList: MutableList<String>,
        grantedPermissions: MutableList<String>,
        deniedPermissions: MutableList<String>
    ) -> Unit)? = null

    /*「授权结果」被授予的权限列表*/
    var grantedPermissions: ((grantedPermissions: MutableList<String>) -> Unit)? = null

    /*「授权结果」被拒绝的权限列表*/
    var deniedPermissions: ((deniedPermissions: MutableList<String>) -> Unit)? = null

    /*「授权结果」所有权限均被授予*/
    var grantedAllPermissions: ((allPermissionList: MutableList<String>) -> Unit)? = null

    /*「授权结果」所有权限均被拒绝*/
    var deniedAllPermissions: ((allPermissionList: MutableList<String>) -> Unit)? = null

    /**二次请求解释
     * @param grantedRequestList 「二次权限请求」的权限请求列表中 包含的 「已经被授予」的权限
     * @param firstRequestList 「二次权限请求」的权限请求列表中 包含的 「首次请求」的权限
     * @param secondRequestList 「二次权限请求」的权限请求列表中需要继续请求的权限
     * */
    var secondRationale: ((
        permission: Permission, activity: Activity,
        allPermissionList: MutableList<String>,
        grantedRequestList: MutableList<String>,
        firstRequestList: MutableList<String>,
        secondRequestList: MutableList<String>
    ) -> Unit)? = null

    /**三次请求解释
     * @param grantedRequestList 「三次权限请求」的权限请求列表中 包含的 「已经被授予」的权限
     * @param firstRequestList 「三次权限请求」的权限请求列表中 包含的 「首次请求」的权限
     * @param secondRequestList 「三次权限请求」的权限请求列表中 包含的 「二次请求」的权限
     * @param thirdRequestList 「三次权限请求」的权限请求列表中需要继续请求的权限
     * */
    var thirdRationale: ((
        permission: Permission, activity: Activity,
        allPermissionList: MutableList<String>,
        grantedRequestList: MutableList<String>,
        firstRequestList: MutableList<String>,
        secondRequestList: MutableList<String>,
        thirdRequestList: MutableList<String>
    ) -> Unit)? = null

}