package com.libiao.mushroom.mine.tab

class Tabs {

    val tabInfos = mutableListOf<TabItem>()

    init {
        tabInfos.add(FangLiangTab())
        tabInfos.add(Line20Tab())
        tabInfos.add(BanOneTab())

        tabInfos.add(BanOneChuangTab())
        tabInfos.add(BanTwoChuangTab())
        tabInfos.add(BanTwoTab())
        tabInfos.add(BanThreeTab())
        tabInfos.add(BanFourTab())
        tabInfos.add(BanFiveTab())
        tabInfos.add(BanSixTab())


        tabInfos.add(MineTab())
    }

    fun position(item: TabItem?): Int {
        tabInfos.forEachIndexed { index, tabItem ->
            if(tabItem == item) return index
        }
        return 0
    }
}