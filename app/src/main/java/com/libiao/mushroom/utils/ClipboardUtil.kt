package com.libiao.mushroom.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast


class ClipboardUtil {

    companion object {
        private var cm: ClipboardManager? = null

        fun clip(context: Context, content: String?) {
            if(cm == null) {
                cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            }
            val clipData = ClipData.newPlainText("Label", content)
            cm?.primaryClip = clipData
            Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
        }
    }
}