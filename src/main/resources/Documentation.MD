#Documentation

本 Documentation 由 @yhluk 在办公室摸鱼没事情干时撰写。

##目录
+ 简介
+ 文件结构
+ 功能列表

##简介
LieLieTea-Bot 基于 [Mirai](https://github.com/mamoe/mirai) 由 @MarbelGateKeeper 开发（和我基本没啥事），虽然有一定争议但是还是号称是为简体中文兽人社群（Mandarin-spoken Furry Community）服务的。
###有关Mirai
时至今日我们依旧不知道Mirai为什么没有跑路，总之就是用了。Mirai的 [开发文档](https://docs.mirai.mamoe.net/) 虽然写的对初学者很不友好但是得看。

根据我粗鄙的理解，全局监听系统`GlobalEventChannel.INSTANCE`是整个系统的精髓，通过对不同`Event`的监听（比如群消息 `GroupMessageEvent` 或者机器人加入群聊事件 `BotJoinGroupEvent` ），Bot 才得以有机会实现各种功能，

###有关七筒
七筒~~Chitung~~设定上是烈烈茶的店长。设定由 @青蛙奥利奥 绘制，目前依旧在进行中。初衷是为QQ群聊增加一些乐趣~~并学点java~~。

###有关LLT-Bot的投放
####非盈利
尽管我在这个项目上看到了商业可能，但一定不会这么做，并尽可能维持Bot的中立性。服务器费用会由我持续承担。

####服务器部署
目前Bot只运行在我电脑上，未来会部署在服务器上常态化待命。（我还是倾向于部署在境内）

##文件结构
我相信老唐上手时一定很我一样完全不知所以然，因此我在这里简单介绍一下~~目前的~~文件结构。

目前在目录下的分别为四个软件包+一个Main:

+ `admintools` 管理员工具
+ `messageresponder` 消息回复（主要的内容实现）
+ `utils` 工具类（诸如有关匹配消息信息、匹配ID的类在这里）
+ `viponly` VIP（我不清楚为什么他独立出来了）
+ `JavaPluginMain` Main

使用到的json所在的位置
`src` > `main` > `resources` > `cluster`

##功能列表
###Admin Tools
`管理` `管理权限`
用来获取有关 Bot 的数据的工具。Bot通过QQ号检测是否是管理员账号发送的消息。

目前进度：只实现了`/group`和`/friend`返回Bot所在的群信息和好友信息。

计划开发：`Log`系统，以及用于获得对应数据的命令，诸如`/data week`(周报)。
###Dice
`被动` `非权限`

一个非常耐艹骰子。

召唤示例：`.5d100` `/dice10`

###Ok Pizza / 吃什么
`被动` `非权限`

从 `src` > `main` > `resources` > `THUOCL` > `THUOCL_food.txt` 中随机返回食物来制作披萨和饭。

词库来源于 [清华大学开源中文词库](http://thuocl.thunlp.org/) 。

###奶茶 / 喝什么
`被动` `非权限`

用QQ号和日期每小时生成随机饮品。

###检测关键词
`被动` `非权限`
####Anti-Overwatch
检测是否提到守望先锋。狗都不玩。~~现在的回复是不是有点太具攻击性了？~~
####Anti-Dirty-Words
检测脏话。
####GoodBye
检测告别语句。
###守望台词
`被动` `非权限`

返回守望先锋大招台词。

###Ok Bummer
`被动` `权限`

通过 `Ok Bummer` 来随机选取群众的任意成员，该成员和功能触发人同时被禁言。

###Ok Winner
`被动` `非权限`

通过群号和当天日期计算出本群的幸运观众。

目前进度：抽得出来。

计划开发：用幸运观众的头像生成一张图片，然后加点 Bling Bling 的效果。

###Ok C4
`被动` `权限`

可能会修改随机机制，目前是以“1/群成员数量”的概率在试图引起全群禁言。

概率提升：增加到“1/群成员数量开方”。

计划开发：要不要顺便发个炸弹图片助助兴？

###Ok Doggie
`被动` `非权限`

使用狗狗图片接口返回狗狗图片。第一版调用 dog.ceo 的接口是成功的，现在的似乎有一些问题需要修复。

狗狗图片接口：
+ https://place.dog/
+ https://dog.ceo/api/
+ https://shiba.online/
+ https://random.dog/

问题：dog.ceo的响应速度非常慢（>10秒），并且第一版测试中只对FriendMessageEvent生效，GroupMessageEvent不起作用，不清楚原因。四个网站中反应速度最快的是random.dog。

计划开发：要不要搞个谁快用谁的？如果一直都是random.dog也不用整代码了直接就用它吧。

###麻将占卜
`被动` `非权限`

用日期和QQ号生成数并从144张麻将牌中选取一张来占卜。

目前进度：可以用了

计划开发：返回一张麻将图片（我看中了Wikipedia上的资源，但是Wiki肯定没法直接互联网抓取，要不要放在包里呢？）

###Broadcast
`主动` `非权限`
以特定时间间隔向一个群聊列表内的群发送消息。出于安全考虑，该消息不能完全一致，且发送间隔不能过快。