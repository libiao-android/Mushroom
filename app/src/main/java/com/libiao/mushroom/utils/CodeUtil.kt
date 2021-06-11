package com.libiao.mushroom.utils

class CodeUtil {
    companion object {
        fun getSzCode(i: Int): String {
            if(i < 10) return "sz00000$i"
            if(i < 100) return "sz0000$i"
            if(i < 1000) return "sz000$i"
            if(i < 10000) return "sz00$i"
            return "sz000001"
        }

        fun getShCode(i: Int): String {
            if(i < 10) return "sh60000$i"
            if(i < 100) return "sh6000$i"
            if(i < 1000) return "sh600$i"
            if(i < 10000) return "sh60$i"
            return "sh000000"
        }

        fun getCyCode(i: Int): String {
            if(i < 10) return "sz30000$i"
            if(i < 100) return "sz3000$i"
            if(i < 1000) return "sz300$i"
            if(i < 10000) return "sz30$i"
            return "sz300000"
        }

        fun getKcCode(i: Int): String {
            if(i < 10) return "sh68800$i"
            if(i < 100) return "sh6880$i"
            if(i < 1000) return "sh688$i"
            return "sh688001"
        }
    }
}