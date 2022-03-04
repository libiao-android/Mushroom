package com.libiao.mushroom.mine

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.libiao.mushroom.R
import kotlinx.android.synthetic.main.time_item_view.view.*

@ModelView
class TimeItemView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
): RelativeLayout(context, attributeSet, defStyleAttr) {

    @ModelProp
    fun setTime(time: String?) {
        time_item.text = time
    }
    @ModelProp
    fun expand(expand: Boolean) {
        if(expand) {
            iv_time_expand.setImageResource(R.mipmap.icon_green_arrow_up)
        } else {
            iv_time_expand.setImageResource(R.mipmap.icon_green_arrow_down)
        }
    }

    @CallbackProp
    fun setClick(callback: ((view: View) -> Unit)?) {
        setOnClickListener {
            callback?.invoke(it)
        }
    }
}