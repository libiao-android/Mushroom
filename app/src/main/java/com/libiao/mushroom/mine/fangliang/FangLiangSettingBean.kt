package com.libiao.mushroom.mine.fangliang

class FangLiangSettingBean {

    var type = 0

    var timeChecked: Boolean = true

    var liangLeftChecked: Boolean = false

    var timeValue: Int = 0
    var timeValue2: Int = 999

    var liangLeftValue: Double = 0.00

    var zuiDaLiang: Boolean = false
    var fangLiang: Boolean = false


    var weekOne = false
    var weekTwo = false
    var weekThree = false
    var weekFour = false
    var weekFive = false

    var firstMax = false


    override fun toString(): String {
        return "[$timeChecked, $timeValue]" +
                ", [$zuiDaLiang, $fangLiang]"
    }
}