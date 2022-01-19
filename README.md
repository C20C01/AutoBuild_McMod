# AutoBuild_McMod

*初学Mod开发的练手作品，可能导致游戏崩溃。十分简陋，大佬勿喷。*

搞平面地图画使的东东，为了方便就搞了这个Mod。

版本1.18.1 Forge

## 简介（复制于Mod内的介绍）

自动搭建Mod By：CC2001

### 声明：

    此Mod为辅助Mod，可能会导致包括但不限于：
    * 游戏性降低
    * 游戏崩溃
    * 多人模式被"/ban"
    * 损坏存档
    * 电脑爆炸
    作者概不承担任何责任，
    若继续使用则视为认同上述声明。

### 说明：

    手拿羽毛左键方块，确定基准点、开始搭建。
    手拿羽毛左键空气，加载地图文档，开启建造地图模式。再次左键可以回到普通模式。
    搭建中，手拿木棍左键空气，暂停搭建（长时间暂停），再次左键空气，取消搭建。
    搭建中，手拿羽毛、木棍可暂停搭建，主手无物品时也可暂停（短时间暂停）。
    暂停中，手拿羽毛左键，恢复搭建。
    从暂停恢复搭建时，请站在断点附近，以防距离过长导致移动失败。
    未搭建时，手拿木棍左键空气，调整搭建的大小。默认为8*8方块，最大为128*128。
    新添加自定义模式为初始模式，可选择两点搭建厚度为一格平台（平台Y轴高度以第一个基准点为准）
    大小必须小于128*128，且第二个基准点位于第一个基准点的西南方向，也就是向X轴正方向、Z轴正方向。
    确定完两个基准点后返回第一个点附近，再点击任意方块以开始建造防止防距离过长导致移动失败。

    针对崩溃后断点续建的功能：先确定基点，注意要和之前的完全一样（尤其是Y轴，易被搞错），
    手拿木棍左键上次所建的最后一个方块确定重启点，
    调整搭建的大小与上次的一致后，手拿羽毛左键方块，从最后一个方块开始断点续建。

    搭建范围为基准方块上一格，向X轴正方向、Z轴正方向（可以看看F3界面准心）延伸设置的格数。

    建议在实际使用前，开新地图测试一下。

    普通模式时（未加载地图文档时），按手中所拿方块搭建。
    9个物品栏里除方块、羽毛、木棍以外的任何东西都不要带。
    且羽毛、木棍要放在物品栏最前面。
    当用完一组时，会自动向下一组切换（仅限于9个栏位里的）。
    全部用完后会返回到第一个格子，并会进行提示。

    加载地图文档后，将按照文档所设置的内容通过物品栏（9个栏位）更换方块。
    注意：不要将物品栏全部填满方块，以防遇到特殊情况需要暂停。

    初次使用，地图文档会在羽毛左键空气时建立。
    文档内只能包含0，1，2.....9 十个数字及回车。
    0~8分别对应着1~9的物品栏。
    9对应为空，即不放置方块，建造时会自动跳过。
    文档一数字一回车，第一行对应着第一个方块的位置，第二行对应第二个，以此类推。
    关于地图文档说明后面会有补充。

    开始时请提前在对应的物品栏放上想使用的方块。
    搭建地图画时，以地图左上角为基准即可。

    运行的大体逻辑：
    移动到下一个方块上方>>
    按照文档从1~9的物品栏中进行选择>>
    放置方块>>

### 地图文档的补充说明：

    地图文档的位置一般为'你的游戏文件夹/CCMod/file.txt'
    例如：
    ../.minecraft/CCMod/file.txt
    ../.minecraft/versions/1.16.5_Forge/CCMod/file.txt

    搭建顺序：（以3*3为例）
        1 2 3
        4 5 6
        7 8 9

    文档内容示例：

    7
    7
    8
    7
    7
    9
    9
    7
    8
    8
    9
    7
    9
    8
    7
    8

    成品：（以8号栏为石头，9号栏为圆石为例）

        石头 石头 圆石 石头
        石头 空气 空气 石头
        圆石 圆石 空气 石头
        空气 圆石 石头 圆石


###  其他：

    游戏内的提示为了不出乱码，只能用英文了。受限于本人英语水平，大部分使用机翻，机翻以外就全是我的塑料英语，
    如遇语法错误、用词不当等问题，敬请谅解。

    在使用时可能会出现游戏崩溃的现象。因本人技术有限，暂时无法确定问题所在且无法提供任何有效的解决方案（这就是断点续建的由来）。
    只能先使用断点续建功能治标不治本了，敬请理解。

    功能并不是特别强大，这个只是边学Mod开发，边摸索出来的实验品，不能保证一点Bug都没有，感谢使用。

    QQ：1005798760

2022.1.19

## 关于本Mod的地图文档生成器

    很简单的一个java程序，也放在这个里面了（jpgTotxt.java）,输入一个128*128像素的图片，输出一个写满0\~9的file.txt。
    
##  在博客上有更多的介绍

    https://c20c01.github.io/2021/09/04/mc%E8%87%AA%E5%8A%A8%E6%90%AD%E5%BB%BAMod/
    
## 致谢

    感谢下面两个mod教程的编写者。

    https://harbinger.covertdragon.team/
    
    https://boson.v2mcdev.com/    
