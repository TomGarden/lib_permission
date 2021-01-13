package io.github.tomgarden.lib.permission

/**
 * describe : 枚举标记, 标记请求次数
 *
 * author : Create by tom , on 2020/9/7-10:10 PM
 * github : https://github.com/TomGarden
 */
enum class RequestFlag(val flag: Int) {
    DEF(0),/*标记权限已经被授予*/
    FIRST(1),/*首次请求——无需解释 && 尚未授权 && 尚未进行过权限请求*/
    SECOND(2),/*二次请求——需要解释 && 尚未授权*/
    THIRD(3);/*三次请求——无需解释 && 尚未授权 && 已经进行过权限请求*/
}