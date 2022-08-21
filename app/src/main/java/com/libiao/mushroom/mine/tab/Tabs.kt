package com.libiao.mushroom.mine.tab

class Tabs {

    val tabInfos = mutableListOf<TabItem>()

    init {

        tabInfos.add(Line20Tab())
        tabInfos.add(Up50Tab())
        tabInfos.add(BanOneChuangTab())


        //tabInfos.add(FangLiangTab())
        tabInfos.add(TestTab())

        tabInfos.add(BanTwoChuangTab())
        tabInfos.add(BanSixTab())
        tabInfos.add(BanFiveTab())
        tabInfos.add(BanFourTab())
        tabInfos.add(BanThreeTab())
        tabInfos.add(BanTwoTab())
        tabInfos.add(BanOneTab())

        //tabInfos.add(MineTab())




    }

    fun position(item: TabItem?): Int {
        tabInfos.forEachIndexed { index, tabItem ->
            if(tabItem == item) return index
        }
        return 0
    }
}