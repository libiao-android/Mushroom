package com.libiao.mushroom.mine.tab

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.SelfSelectionActivity
import com.libiao.mushroom.utils.Constant

abstract class TabItem {
    var fragment: Fragment? = null
    var tv: TextView? = null
    abstract fun createFragment(): Fragment
    abstract fun name(): String
    abstract fun tag(): String

    fun textView(context: Context): TextView {
        if(tv == null) {
            tv = TextView(context)
            tv?.text = name()
            tv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, Constant.ORIGIN)
            tv?.setTextColor(Color.BLACK)
            tv?.setOnClickListener {
                if(context is SelfSelectionActivity) {
                    context.onItemClick(this)
                }
            }
        }
        return tv!!
    }

    fun scale(origin: Float, scale: Float) {
        tv?.also {
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, origin + Constant.SCALE * scale)
        }
    }
}