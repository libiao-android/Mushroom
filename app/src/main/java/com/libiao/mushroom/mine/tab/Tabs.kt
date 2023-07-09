package com.libiao.mushroom.mine.tab

class Tabs {

    val tabInfos = mutableListOf<TabItem>()

    init {

        tabInfos.add(TestTab())

        tabInfos.add(Line20Tab())

        tabInfos.add(FanBaoTab())
        tabInfos.add(XinGaoTab())






        //tabInfos.add(JiGouTab())

        tabInfos.add(BanThreeTab())

        tabInfos.add(Up50Tab())

        //tabInfos.add(BanOneTab())




        tabInfos.add(BanOneChuangTab())


        //tabInfos.add(FangLiangTab())


        tabInfos.add(BanTwoChuangTab())
        tabInfos.add(BanSixTab())
        tabInfos.add(BanFiveTab())
        tabInfos.add(BanFourTab())

        tabInfos.add(BanTwoTab())


        //tabInfos.add(MineTab())




    }

    fun position(item: TabItem?): Int {
        tabInfos.forEachIndexed { index, tabItem ->
            if(tabItem == item) return index
        }
        return 0
    }
}