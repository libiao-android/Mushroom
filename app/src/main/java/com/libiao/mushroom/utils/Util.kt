package com.libiao.mushroom.utils

fun baoLiuXiaoShu(value: Double): String {
    return String.format("%.2f", value)
}

fun huanSuanYi(price: Double): String {
    return "${String.format("%.2f",price / 100000000)}亿"
}