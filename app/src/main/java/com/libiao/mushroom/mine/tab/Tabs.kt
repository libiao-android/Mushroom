package com.libiao.mushroom.mine.tab

class Tabs {

    val tabInfos = mutableListOf<TabItem>()

    init {

        tabInfos.add(Line20Tab())

        tabInfos.add(Up50Tab())
        tabInfos.add(BanOneChuangTab())


        //tabInfos.add(FangLiangTab())


        tabInfos.add(BanTwoTab())


        //tabInfos.add(BanTwoChuangTab())

        //tabInfos.add(BanThreeTab())
        //tabInfos.add(BanFourTab())
        //tabInfos.add(BanFiveTab())

        //tabInfos.add(BanSixTab())


        //tabInfos.add(MineTab())

        //tabInfos.add(TestTab())

        tabInfos.add(BanOneTab())


    }

    fun position(item: TabItem?): Int {
        tabInfos.forEachIndexed { index, tabItem ->
            if(tabItem == item) return index
        }
        return 0
    }
}