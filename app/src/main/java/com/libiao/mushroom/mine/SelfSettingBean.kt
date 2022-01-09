package com.libiao.mushroom.mine

class SelfSettingBean {

    var timeChecked: Boolean = false
    var rangeLeftChecked: Boolean = false
    var rangeRightChecked: Boolean = false
    var liangLeftChecked: Boolean = false
    var liangRightChecked: Boolean = false
    var redLine: Boolean = false

    var timeValue: Int = 0
    var rangeLeftValue: Double = 0.00
    var rangeRightValue: Double = 0.00
    var liangLeftValue: Double = 0.00
    var liangRightValue: Double = 0.00

    var fangLiang: Boolean = false
    var maxRangChecked: Boolean = false
    var maxRangValue: Double = 0.00
    var zhiDie: Boolean = false

    override fun toString(): String {
        return "[$timeChecked, $timeValue], [$rangeLeftChecked, $rangeLeftValue], [$rangeRightChecked, $rangeRightValue]" +
                ", [$liangLeftChecked, $liangLeftValue], [$liangRightChecked, $liangRightValue], [$redLine]"
    }
}