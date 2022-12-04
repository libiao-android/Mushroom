package com.libiao.mushroom.mine

class SelfSettingBean {

    var timeChecked: Boolean = true
    var rangeLeftChecked: Boolean = false
    var rangeRightChecked: Boolean = false
    var liangLeftChecked: Boolean = false
    var liangRightChecked: Boolean = false
    var redLine: Boolean = false

    var timeValue: Int = 0
    var timeValue2: Int = 99
    var rangeLeftValue: Double = 0.00
    var rangeRightValue: Double = 0.00
    var liangLeftValue: Double = 0.00
    var liangRightValue: Double = 0.00

    var youXuan: Boolean = false
    var maxRangChecked: Boolean = false
    var maxRangValue: Double = 0.00
    var zhiDie: Boolean = false

    var duan_ceng_Checked: Boolean = false
    var xin_gao: Boolean = false

    var you_one: Boolean = false
    var you_two: Boolean = false
    var you_three: Boolean = false

    override fun toString(): String {
        return "[$timeChecked, $timeValue], [$rangeLeftChecked, $rangeLeftValue], [$rangeRightChecked, $rangeRightValue]" +
                ", [$liangLeftChecked, $liangLeftValue], [$liangRightChecked, $liangRightValue], [$redLine]"
    }
}