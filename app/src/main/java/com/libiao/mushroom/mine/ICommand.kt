package com.libiao.mushroom.mine

interface ICommand {
    companion object {
        const val SETTING = 1
        const val HEART = 2
        const val LOCAL = 3
        const val NETWORK = 4

        const val OBTAIN_SIZE = 5
    }
    fun order(type: Int, data: Any? = null)
    fun obtain(type: Int): Any?
}