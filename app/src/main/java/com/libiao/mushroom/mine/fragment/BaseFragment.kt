package com.libiao.mushroom.mine.fragment

import androidx.fragment.app.Fragment

open class BaseFragment(resLayout: Int): Fragment(resLayout) {

    protected var heartChecked = false
    protected var onLineChecked = false
    protected var loadingStatus = 0
}