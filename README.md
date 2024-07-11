# 灯蕊

## 项目简介

一个自用的摘录 Web App ，基于 GitHub 进行多端数据云同步。

## 技术方案

### Java 后端方案

主要技术：

- 前端： Vue3，Vant4 组件库。
- 后端： Spring Boot3（JDK17），MyBatis 。
- 数据库：SQLite3 。
- 云端同步：使用 Github 存储数据。

#### 后端转换为应用方案（废弃）

- Windows 端使用导出的 exe

	功能不稳定。

- Android 使用导出的 apk 

	不能直接导出，需要对代码进行平台适配。

#### 云服务器方案

- 后端云服务器部署。
- 前端采用网页访问。

### PWA 方案（采用）

- 前端采用 PWA （Progress Web Applicaton）。
- 后端在 PWA 中使用 JS 操作本地存储，并进行云端同步。
- 同时适用电脑和手机浏览器。

## 应用功能

- card 浏览（按生成方式）
- 最近历史
- 浏览全部 card
- 添加 card
- 修改 card
- 删除 card
- 搜索 card
- 云端同步

## 排版约定

- 空格

	- 全部使用半角符号。（中文对齐需要全角空格时除外）
	- 采用齐头式，禁止段首空两格（格式有特殊意义时除外）
	- 对齐只允许使用空格，禁止 Tab 。

- 换行

	- 换行

	  句间不空行（句末一个换行）

	- 换段

	  段间空一行（段末两个换行）
	
	- 注意
	
	  - 使用 Windows 记事本编辑时，两个回车需要使用 `\n\r` 进行换段替换。
	  - 使用 Vant4 的输入框输入时，两个回车需要使用 `\n\n` 进行换段替换。

## 数据库设计

### card 设计

#### 卡片

- 卡片（card）

- 类型（type）
- 内容（content）

#### 类型

- 文学（literature）（0）
	
	有书的。
	
	- 诗歌（poetry）
	- 文章（article）
	- 小说（fiction）
		- 包括轻小说。
	
- 歌曲（song）（1）

	重点在歌词。

- 言语（words）（2）

	- 出处不是诗歌、文章，小说的句子。
		- ACG：动画、电视剧、电影。
		- 剧目：音乐剧，歌剧，舞台剧，话剧，广播剧，朗读剧等。
	
	- 出处、类型不明晰的句子。
	

由原作改编的各类作品，以改编后的作品类型为准。

#### 描述

各类型的描述：（觉得不重要的信息可以留空）



文学（诗歌、文章、小说）：

- 标题（title）

- 译题（translation title）

- 原文（original text）

- 译文（translation text）

- 作者（author）

- 译者（translator）

- 书籍（book）

- 版本（edition）

	书籍相关的的出版社、版次等信息。

- 备注（note）




歌曲：

- 标题（title）
- 译题（translation title）
- 原文（original text）
- 译文（translation text）
- 作词（lyric）
- 译者（translator）
- 演唱（voice）
- 作曲（compose）
- 编曲（arrangement）
- 专辑（album）
- 出处（source）

- 备注（note）




言语：

- 原文（original text）

- 译文（translation text）

- 语者（sayer）

  说话人，可以是角色。

  - 注意，言语的 sayer 可为空。

- 译者（translator）

- 出处（source）

  描述来源的文字，范围较广。

  - 没有必要再具体指定细分类，有一个显著的出处描述即可。
  
- 备注（note）

#### 原文、译文

原文和译文整体分开展示：

- 原文和译文往往不是逐句对应的。
- 分开展示以保留原文整体的「形」。



副标题、章节标号等，写在「原文」、「译文」中。



语言约定：（UTF-8）

- 中文（华文楷体：STKaiti ）
- 日文（华文楷体：STKaiti ）
- 英文（Gadugi）

### 历史记录

历史记录分类：（type）

- read（0）
- add（1）
- update（2）
- delete（3）
- readInAll（4）（浏览全部 card 功能）



### 建表语句

```sql
/* 建表 */
drop table if exists card;
create table card (
                      id integer primary key autoincrement ,
                      type integer
                          not null
                          check( type in (0, 1, 2)) , -- 0:文学 1:歌曲 2:言语
                      content_id integer
                          not null ,
                      create_time timestamp ,
                      update_time timestamp ,
                      deleted integer

);

drop table if exists literature;
create table literature (
                            id integer primary key autoincrement ,
                            title text ,
                            translation_title text ,
                            original_text text ,
                            translation_text text,
                            author text ,
                            translator text ,
                            book text ,
                            edition text ,
                            note text,
                            create_time timestamp ,
                            update_time timestamp ,
                            deleted integer
);


drop table if exists song;
create table song (
                      id integer primary key autoincrement ,
                      title text ,
                      translation_title text ,
                      original_text text ,
                      translation_text text ,
                      lyric text ,   -- 作词
                      voice text ,
                      compose text ,
                      arrangement text ,
                      album text ,
                      source text ,
                      note text,
                      create_time timestamp ,
                      update_time timestamp ,
                      deleted integer
);


drop table if exists words;
create table words (
                       id integer primary key autoincrement ,
                       original_text text ,
                       translation_text text ,
                       sayer text ,
                       translator text ,
                       source text ,
                       note text,
                       create_time timestamp ,
                       update_time timestamp ,
                       deleted integer
);

drop table if exists history;
create table history (
                      id integer primary key autoincrement ,
                      cardId integer ,
                      type integer
                        not null
                        check ( type in (0, 1, 2, 3, 4)), -- 0 read, 1 add, 2 update, 3 delete, 4 readInAll
                      create_time timestamp
);

drop table if exists github_info;
create table github_info (
                         id integer primary key autoincrement,
                         username text,
                         repo_name text,
                         access_token text
);
insert into github_info values (null, null, null, null);


/* 测试数据 */

insert into card values (null, 0, 1, datetime('now', 'localtime'), datetime('now', 'localtime'), 0);
insert into literature
values (null, null, null, null, '我要唱的歌，直到今天还没有唱出。
每天我总在乐器上调理弦索。
时间还没有到来，歌词也未曾填好，只有愿望的痛苦在我心中。
花蕊还未开放，只有风从旁叹息走过。

我没有看见过他的脸，也没有听见过他的声音；我只听见他轻蹑的足音，从我房前路上走过。
悠长的一天消磨在为他在地上铺设座位；但是灯火还未点上，我不能请他进来。
我生活在和他相会的希望中，但这相会的日子还没有来到。', '泰戈尔', '冰心', '飞鸟集', '译林出版社 出版年: 2010/6', '具体出自 吉檀迦利',
        datetime('now', 'localtime'), datetime('now', 'localtime'), 0);
insert into history values (null, 1, 1, datetime('now', 'localtime') );


insert into card values (null, 1, 1, datetime('now', 'localtime'), datetime('now', 'localtime'), 0);
insert into song
values (null, '迷星叫', null, '迷星叫的日文歌词', '迷星叫的中文歌词', null, 'MyGO!!!!!', null, null, '迷迹波', 'BanG Dream', null,
        datetime('now', 'localtime'), datetime('now', 'localtime'), 0);
insert into history values (null, 2, 1, datetime('now', 'localtime') );


insert into card values (null, 2, 1, datetime('now', 'localtime'), datetime('now', 'localtime'), 0);
insert into words
values (null, '我看见花，绝非美丽；我知道爱，绝非真意', null, '博丽未灯', null, '繁度论', null,
        datetime('now', 'localtime'), datetime('now', 'localtime'), 0);
insert into history values (null, 3, 1, datetime('now', 'localtime') );


/* readInAll */
insert into history values (null, 1, 4, datetime('now', 'localtime') );
insert into history values (null, 2, 4, datetime('now', 'localtime') );
insert into history values (null, 3, 4, datetime('now', 'localtime') );
```

## 实现细节

### 组件元素名称

vant 的全局导入和局部导入时，组件元素的名称有不同。

```
# 全局导入

# use
<van-swipe> </van-swipe>


# 局部导入

<script setup>
import { Swipe } from 'vant';
</script>

# use
<swipe> </swipe>
```

### 换行间隔

- 主动换行

	人为输入换行符。

- 自动换行

	行长度过长自动换行。

需要保证主动换行比自动换行的间距更大：

- 将每行单独划分为一个元素。
- 使用 margin 增大各行的间距。
- 减小自动换行的行的行高。

### 滑动浏览

基于 Vant 的轮播图实现滑动切换浏览页面：

- 上一个 card

  浏览记录。

  - 历史记录的查看行为不记入历史记录。
  
- 当前 card

	记入历史记录。

- 下一个 card

	下一个 card 号码由后端生成。（暂定随机）

没有在 vue 部分做缓存，而是利用浏览器自身对 url 的缓存。

如果自己实现，需要对移动端做滑动事件适配（Web 端是鼠标事件）。

### 浏览历史记录时的下标

#### 需求

可以左滑回看任意个历史记录。

#### 问题描述

如果直接创建大量的轮播图页面，资源消耗会非常大，但如果只一次创建部分，组件对下标的支持存在限制。

滑动浏览时，如果初始 index = 1，向 swipe 左侧添加 item 后：

- index 向左翻页变为 0 后就无法再减小，无法再访问新增的 item 。
- 原 index 位置对应的 item 不变。

组件本身的特征：

- 向左翻页 index 变小，最小为 0 。
- 向右翻页 index 变大。

在不修改组件源码的前提下：

- 无法直接修改 swipe 的 initial_index 。
- 无法改变组件内部的当前页 index 。

#### 解决方案

利用 swipe 的循环轮播功能。

在 loop 中，index 会循环变化。

- 保持 swipe 的数据数组长度一直为 3，作为一个局部 swipe 状态，index 会 0,1,2 循环变换。
- 根据 index 变换，就可以区分向左还是向右。
	- 向左翻页，回看历史。
	- 向右翻页，随机新的 card 。
	- 这样就解决了 index 变化到左端点（0）就无法再变化的情况。
- 进一步的，跟随 index 的变化，调整应当元素在 localSwipeCardIds 中的位置。

以上，根据当前卡片内容，不断更新 swipe 的数据数组的 localSwipeCardIds ，即可实现局部 swipe 状态的移动，即可利用轮播图的滑动效果达到无限滑动。

另外，实际浏览历史记录不会回看太多，实际只提供之前 30 页的回看，以减少无用数据量。

边界情况：

- 左端点时，阻止向左翻页的右滑事件，使得无法再向左翻页。[实现参考](https://blog.csdn.net/duanhy_love/article/details/121925064)
- 右端点时，新增 card 。

注意，该警告不用处理：（这种方法使用后会未生效：[参考](https://www.jianshu.com/p/23850d4cade8)）

```
[Violation] Added non-passive event listener to a scroll-blocking <some> event.
Consider marking event handler as 'passive' to make the page more responsive. See <URL>
```

### 响应式问题

#### 问题

swipe 的轮播项显示内容没有随翻页正常更新。

#### 原因一

swipe 对数据列表没有正常响应式更新。（不是该原因，实验验证是正常响应式更新的）

#### 原因二

翻页后，父组件给子组件的 CardView 传入新的属性时，由于 CardView 内没有设置侦听属性变更，从而没有被重新渲染。

#### 解决方式

给 CardView 添加侦听器（watch），监测属性值的变化，变化后请求新的 card 数据。

#### 经验

更新数据导致的组件更新是重新渲染，而不是重新创建。（注意生命周期）

### 生成 newCardId

#### 需求

- 采用随机生成，但不能与历史记录中最多 10 条重复。
- 如果无法生成无重复的 cardId，则返回 null （没有更多 card）。

#### 分析

在已有 card 较多时，随机生成非常容易。

在已有 card 非常少甚至小于 10 时，生成时很容易发生与历史记录的碰撞：

- 此时生成无重复的 cardId 比较困难，资源消耗也对。

#### 解决方案

修改需求：

- 如果 10 次仍然碰撞，则返回和当前正在浏览的 card 不一样的 cardId 即可。
- 边界情况时返回 null 。（没有和正在浏览的 card 不一样的 cardId）

### 历史记录

加入历史记录的时机：

- 创建 Swipe 时。（页面刷新会导致重新创建 Swipe）
- 访问最新阅读的 card 时。
- 添加 card 时。
- 在浏览全部 card 功能中，记录当前最新浏览位置。

注意：

在路由切换跳转之后，页面会刷新。

### Swipe 组件与 TabBar 的联动

浏览最近历史记录时，使用 Vuex ，从 Swipe 向平级的 TabBar 传递信息。

swipe 传递以下状态信息：

- globalPresentIndex
- globalMostNewReadIndex

直接用计算属性监听 vuex 这两个值的变化，然后 `return globalPresentIndex < globalMostNewReadIndex ` 。

### Toast 组件不显示问题

#### 描述

Toast 组件元素存在，但不显示。

#### 原因

Vant4 的 Toast 是函数式组件，需要手动引入样式。[详见](https://blog.csdn.net/train__00/article/details/128671381)

#### 可能方案一

是 Vant4 组件自身的问题，解决方法为引入样式文件：

```js
// 引入其中一个即可

import "vant/lib/index.css"  // 组件不显示样式问题，针对所有组件

import 'vant/es/toast/style' // 针对 Toast 组件
```

但这样引入会是全局样式，会污染到其它样式。

#### 可能方案二

尝试自己写一个 MyToast ，将样式作用域限制在 MyToast 内。

失败，没有找到限制的方法。在 `style` 中使用 `@import "vant/lib/index.css";` 仍然是全局样式。

如果使用 `scoped` [参考](https://segmentfault.com/a/1190000012728854)，Toast 仍会不显示，说明 Toast 必需使用全局样式。

#### 解决方案

自己写一个简单的提示，直接使用绝对定位的 div 。

### 跳转回最新页

#### 方案一（废弃）

直接使用刷新实现。

使用 provide 和 inject 传递方法，使用 nextTick 产生微任务重新创建组件。[参考](https://www.php.cn/faq/505789.html)

#### 方案二

自行抽象出 CardSwipe 组件后，直接响应式改变 CardSwipe 的数据即可。

### 移动到顶部

将元素 scrollTop 属性值设为 0 即可。

注意，必须是控制滚动条的那个元素的该属性才行，其它元素 scrollTop 可能一直为 0 。

### textarea 焦点问题

#### 描述

对于（vant4）开启了 autosize 属性的 textarea 。

输入时如果触发了 autosize ，此时需要点击外部才能失去焦点。

如果没有先点击外部失去焦点，而是：

- 直接提交按钮，则页面跳动，失去焦点，没有按钮点击事件。
- 直接其它输入框，则页面跳动，旧框失去焦点，当前输入框获得焦点。

#### 测试

focus 在一个 textarea 上时:

- 点击另一个 textarea 时，会先 blur ，然后跳动，然后 focus 过去。
- 点击提交，只会 blur 。
- click-input 事件发生在 focus 后。

实验可知：

- autosize 使输入框加行时，加大了整体页面的高度，但加行带来的增加的高度没有被修改到元素的属性中。
- blur 时视口会回到元素的 scrollTop 值的状态。
- 当滑动到底部点击提交时，或滑动到下方点击另一个 textarea 时，元素记录 scrollTop 值会比真实的 scrollTop 值小，从而页面会向上跳动。

#### 解决方案一

对于直接点击按钮：（该点已是最终方案）

- blur 事件由 mouseDown 触发，将按钮上的 click 改为 mouseDown 并阻止其默认事件（阻止 blur）即可使得按钮生效。（此时输入框没有 blur）[参考](https://blog.csdn.net/weixin_56242672/article/details/128972223)

对于直接点击其它输入框（textarea）：

- mousedown 事件发生在 blur 前。
- focus 发生在跳动后。
- 在点击输入框时，保持视口在当前最新位置。（跳动后立刻跳动回来，但肉眼看不见）
- 记录 mousedown 时的 scrollTop ，然后再 focus 时跳动该位置。

#### 解决方案二

方案一可以解决：有大量内容的「原文」框跳到「译文」框的情况；（1）

但解决不了：有大量内容的「原文」框跳到有大量内容「译文」框的跳动情况。（2）

进一步分析发现：在（2）时，跳动发生的时机在 focus 后，会在 focus 后更新到较小的组件内部记录的 scrollTop 值。

所以方案二：

- focus 后设定一个延迟时间再跳到正确的位置。（1 ms）（该点已是最终方案）

	跳转距离越长，页面闪动越剧烈，但仍然是轻微闪动。

#### 解决方案三

mousedown 到 focus 后的一小段时间内，禁止滚动。

- blur 启用禁止滚动。
- focus 后一段时间，允许滚动。

失败，通过 vue3 动态控制 css 样式，设置 overflow-y 为 hidden 后，页面仍然会跳动。

#### 最终方案

对于直接点击按钮：

- blur 事件由 mouseDown 触发，将按钮上的 click 改为 mouseDown 并阻止其默认事件（阻止 blur）即可使得按钮生效。（此时输入框没有 blur）[参考](https://blog.csdn.net/weixin_56242672/article/details/128972223)

对于直接点击其它输入框（textarea）：

- focus 后设定一个延迟时间再跳到正确的位置。（1 ms）（该点已是最终方案）
- 最后的缺点是：跳转距离越长，页面闪动越剧烈，但仍然是轻微闪动。

### 点击 button 颜色闪动被阻止

一般的 button 颜色闪动可用 css 伪类选择器实现：

```css
.button:active {
    background-color: gray;
}
```

问题：（对于 enterPreview 的 button）

- mouseDown 的默认事件被阻止，用于阻止 blur 事件。
- button 的点击激活状态（active ）的颜色变换事件发生在 mousedown 之后。
- 这会导致进入 preview 后 button 才变色。

解决方案：

- 设置 timeout ，先等 button 变色后再进入 preview 。

### 刷新导致的路由问题

#### 问题描述

在「添加」路由页时，浏览器 url 为 `http://localhost:8081/#/add` 。

此时刷新，浏览器 url 保持不变（问题一），但组件会随之刷新，从而页面导航栏变化到默认的 item 「随机」，但页面显示的仍然是「添加」路由页的内容（问题二）。

#### 解决方案

可以使用路由传参解决，但应用在使用时，不会存在这种使用应用没有提供的方式刷新。（浏览器刷新属于应用以外提供的刷新方式）

直接忽略该问题。

#### 时间戳辅助字段

时间戳使用 SQlite 的 timestamp ：

- date 默认为一般日期。
- time 默认为 UTC 时间 + 0 时区。

在 SQL 语句中，可以使用 `datetime('now', 'localtime')` 获得当前系统时区的时间（本地时间）。（包括 Mapper 处的语句）（获取到的系统时间是数据库服务器所在地的系统时间）

在 Java 语句中，可以使用 AOP 对公共字段进行填充，也可以对公共字段进行一些处理，包括指定时区，格式转换等操作。（获取到的系统时间是 java 后端服务器所在地的系统时间）

### enum 还是 constant

OperationType 只用于切入方法的注解，具体是什么值不重要，使用 enum 。

CardType 和 HistoryType 的值需要提交到数据库内，需要知道具体值，使用 constant 。

### 关键词高亮

#### 需求

搜索后显示的内容中，将关键词高亮。

#### 方式一

整个替换 overviewList 的 innerHTML 中的 keyword 为新的标签内容：

```html
'<span class="highlight">'+ keyword +'</span>'

.highlight {
  background-color: yellow;
}
```

##### 问题

会丢失事件。

#### 方式二

遍历 overviewList 下所有元素，找到所有 textNode 。

然后将其 parentNode 的 innerHTML 替换为方式一中的新的标签内容。

注意：

```html
<p>
    文本
</p>
```

以上标签中的“文本”为一个 textNode，它的 parentNode 为 `<p>` 。

#### 方式三（采用）

遍历 overviewList 所有元素计算量大，并且会搜索到和 card-content 不相关的内容。

对 card-content 相关内容添加类属性，直接获取含有该类属性的元素，替换其 innerHTML 。

#### 补充

前端使用正则表达式忽略大小写，并用 replace 函数进行替换。

后端使用 lower 函数转换为小写再判断是否存在（`instr()`）。

### 使用 defineModel()

需要开启配置：[参考](https://liubing.me/article/vue/vue3-defineModel.html#%E5%90%AF%E7%94%A8-definemodel)

```js
// vue.config.js
module.exports = {
  chainWebpack: (config) => {
    config.module
      .rule('vue')
      .use('vue-loader')
      .tap((options) => {
        return {
          ...options,
          defineModel: true
        }
      })
  }
}

```

### 输入框内容提示的间隔监听

#### 需求

输入值后，值在半秒内没有变化再查询提示数据。

#### 方法

watch 监听，如果没有定时器，则设置定时器（半秒），否则重新设置定时器的时间为半秒。

### 输入框内容提示的关闭

#### 需求

- 点击提示项关闭
- 点击其它地方关闭
- 输入框聚焦重新打开

#### 方法一

组件内定义一个透明 cover。

缺点：会影响其它位置组件的某些功能。

#### 方法二

组件内监听点击事件的传播，判断监听到 click 事件的当前 tips 是否归属于事件的 `e.target` 元素（即 input 元素）。

组件内控制显示与否。

缺点：麻烦，效率低。

#### 方法三（采用）

将 input 和提示框抽象成一个组件。

直接利用 input 失焦事件，当前 input 失焦则隐藏提示框，聚焦则显示提示框。

##### 问题

对于点击其它地方关闭功能。点击提示项时，会先 blur 再触发 click 事件，这导致 blur 后提示框消失（blur 事件中会关闭提示框），无法触发 click 完成提示信息到 inputValue 的同步。

##### 解决方法一

blur 后间隔一段时间再关闭提示框。

缺点：会产生一定的延时。

##### 解决方法二（采用）

blur 事件由 mouseDown 触发，将 click 改为 mouseDown 并阻止其默认事件（阻止 blur）即可。（此时输入框没有 blur）

### 逻辑删除时的历史记录

#### 场景

逻辑删除时，history 中会添加 type = 删除 的历史记录条目。

#### 问题

通过 history 查询初始 Card 列表时（在 GenerateCardView 中），history 中已被删除的 Card 的历史记录会被查询出来。

#### 方式一

插入删除历史记录时，将所有历史记录置为 deleted = 0 。

问题：会破坏 deleted 字段的意义，该条历史记录应当是有效的记录，只是记录了删除而已。

#### 方式二（采用）

查询历史记录时，使用 type 为 删除的条目，过滤历史记录中已经被删除的 Card 对应的条目。

过滤的方法：

- 原则：记录删除的历史条目，必定是该 Card 相关的最后一条历史条目。
- 方式：数据库查询可以使用 limit ，对数据从后往前扫描，一边记录  type 为 删除的条目的 CardId ，一边过滤即可。（数组删除操作比较占用时间，所以相对地，直接将满足条件的 CardId 放到新的数组中）

### 逻辑删除时的 update

#### 问题

如果直接新增 Card ，然后将旧 Card 的 deleted = 0 ，那么 Card 的 id 会变为新的 CardID 。

#### 方法

添加 deleted = 1 的词条进行备份，原来的词条做修改。

### 多端数据同步

[GitHub 仓库 API](https://docs.github.com/en/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents)

#### 目标

动态切换 SQLite 的数据库文件。

#### 方式一（不支持）

利用 Spring 的 AbstractRoutingDataSource 配置动态数据源，只能在配置文件已经写入好的数据源列表中动态切换。

#### 方式二（失败）

尝试直接替换文件。

可能会出现文件占用，无法操作。

缺点：SQLite 单个数据库文件 100 KB 以上，流量压力大。

#### 方式三（采用）

每个卡片使用单个 Json 文件存储，每次启动，重建到 SQLite 中。

本地保存 SQLite 文件，每次只需要重建本地没有的或者更新后的卡片 json 。

card json 文件名定义：

```txt
defination:
<cardId>.<the update datetime of the card contained by this json file>.<logistic delete flag, deleted = 1>.json

example:
2.2024-06-26T18:47:49.0.json
3.2024-06-26T18:47:49.1.json
```

后端：

- 增

	json 文件名使用 card 的 updatetime 。

	- 弃用方案

		如果 datetime 使用创建 json 文件的时间，会导致反复同步其它终端上传的新内容。（本地和云端的 card 永远相差大于 10 秒）

- 删

	逻辑删除，保留旧版本。

	- 通过 datetime 区分新旧版本。

	- 删除时通过 deleted flag 表示 json 对应的卡片是否已删除。（弃用）

		逻辑删除时，不方便修改 GitHub 的文件名（官方文档中没有给出直接的修改文件名的方式）。

	- 如果最新 datetime 的 flag 为 1，表示该 card 已经完全删除。（采用）

		每个 card 只考虑最新的 json 文件即可。

- 查

	最新 datetime 的 json 文件才有效。

- 改

	直接新增一个新的 datetime 的 json 文件即可。

- 每次进入 app 时统一同步（这样可以通过单一云端支持多终端的同步）

	- 本地新的，更新到云端。

	- 云端新的，更新到本地。

	- 不同步的内容：

		- card 的历史版本

			因为两次同步之间的多次变动的概率极小，所以一次同步只同步最新版本数据。

		- 浏览历史

	- 比对方法

		1. 获取 SQLite 内的（cardId，updatetime, deleted）和 json 文件名映射（cardId，datetime, deleted_flag）。

		2. 同一个 cardId 下，比对最新 updatetime 和最新 datetime 。

			- 如果相差小于 10 秒，视为同一版本，不进行同步。

			- 如果相差大于 10 秒，较新的 time 对应的 card 作为最新版本，进行同步。

				即使因为一些网络的原因导致同一 card 的本地数据库的 updatetime 和云端的 datetime 相差大于 10 秒了，这样的 card 也不会太多，并且不会破坏数据一致性。

				- 需要同步 delete 的 card

					deleted_flag = 1 的 card 。

				- 需要同步 add 的 card

					本地和云端没有相同 cardId 的 card 。

				- 需要同步 update 的 card

					本地和云端存在相同 cardId 的 card 。

	- 方法对比（弃用方案）

		比对最新 updatetime 的 card 和最新 datetime 的 card 的内容，如果不一致，以较新的那个 time 为最新版本。（这样可以在 updatetime 和 datetime 不一致时正常同步，但是要大量比对 card 的内容）

前端传递到后端：

- 用户自行建立公有仓库
- 上传 用户名，仓库名，Access Token
- 存到 SQLite 里。

Personal access tokens (classic) 需要的 scope 权限：（否则报 404）

- repo
- workflow

### 初始化 GitHub 信息

#### 场景

GitHubFileManager 的仓库信息需要由前端上传。

#### 方式一（错误）

不能在 GitHubFileManager  的构造函数中查询仓库信息。

因为在执行 GitHubFileManager 的构造函数时，syncMapper 还没有注入。

```java
    @Autowired
    SyncMapper syncMapper;

    public GitHubFileManager() {
        GitHubInfo gitHubInfo = syncMapper.getLatestInfo();
        if (gitHubInfo == null) {
            System.out.println("GitHub information does not exist!");
            return;
        }
        owner = gitHubInfo.getUsername();
        repo = gitHubInfo.getRepoName();
        accessToken = gitHubInfo.getAccessToken();
    }
```

#### 方式二（采用）

Spring 会在完成依赖注入和初始化 Bean 后调用 @PostConstruct 标注的方法，可以在其中进行数据库查询或其他初始化操作。

```java
    @Autowired
    SyncMapper syncMapper;

    @PostConstruct
    public void init() {   // 在 GitHubFileManager 初始化完成后自动调用，设置其信息。
        GitHubInfo gitHubInfo = syncMapper.getLatestInfo();
        if (gitHubInfo == null) {
            System.out.println("GitHub information does not exist!");
            return;
        }
        owner = gitHubInfo.getUsername();
        repo = gitHubInfo.getRepoName();
        accessToken = gitHubInfo.getAccessToken();
    }
```

### 自动打开浏览器（弃用）

#### 需求

进入 jar 打包的 exe 或 apk 后，需要自动打开默认浏览器进入 `localhost:8087` 并自动全屏。

#### 前端方式

使用 JS 在进入网址时自动全屏页面。

#### 后端方式一（失败）

Spring 加载完成后，在事件监听器（ApplicationListener）中使用 java.awt.Desktop 类打开浏览器。

问题：

Spirng 中没有桌面的访问权限，不支持。

移动端不支持 Desktop，需要其它方式。

#### 后端方式二（采用）

- 平台检测

一种粗糙的方法，判断系统名称里是否含有操作系统的关键字（系统名称可人为更改，但一般情况下都有效）

```java
String os = System.getProperty("os.name").toLowerCase();

if (os.contains("win")) {
    System.out.println("Windows 操作系统");
} else if (os.contains("mac")) {
    System.out.println("Mac 操作系统");
} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
    System.out.println("类Unix操作系统");
} else if (os.contains("android")) {
    System.out.println("安卓设备");
} else {
    System.out.println("未知操作系统");
}
```

- 电脑端

	使用 Runtime 类执行命令。

```java
String url = "http://localhost:" + port;
Runtime.getRuntime().exec("cmd.exe /c start " + url);   // for Windows
```

- 移动端（Android）

	Java web 程序无法直接打开 Android 系统的浏览器，需要其它手动方案替代。

### 关闭程序（弃用）

#### 需求

前端使用 js 自动全屏页面，同时在前端添加关闭程序按钮，点击后自动关闭前端和后端。

#### 方式一（失败）

- 关闭后端

	前端请求后端程序关闭。

- 关闭前端页面

	使用 JS，但 JS 无法关闭非自己打开的前端页面（浏览器安全策略）。尝试多种替代方案无效。

#### 方式二（采用）

放弃自动全屏页面，用户通过浏览器关闭当前页面。

当前页面关闭时，请求后端关闭。（页面关闭不会触发 Vue3 的 beforeUnmount 和 unmounted 生命周期，需要监听浏览器的关闭页面事件）

##### 监听事件

需要区分刷新和关闭标签页。

通过监听 `beforeunload`（刷新或关闭前） 和 `unload`（刷新或关闭后）事件的**时间差**来区分标签页的刷新和关闭操作。

刷新与关闭都会先后 onbeforeunload 与 onunload，但刷新在加载新页面前还需要一些准备工作，所以刷新事件在执行到 onunload 事件时，用的时间会比关闭事件时间长。

用一个接近空白的测试页面进行测试：

- 关闭时 onbeforeunload 与 onunload 的时间差一般会在 3 毫秒内。
- 刷新时的时间差一般会在 10 毫秒以上。

本项目用 8 ms 作为边界值，小于 8 毫秒为关闭（取一个较大的值，保证后端关闭），大于为刷新。

##### 关闭页面时发送请求

[参考](https://www.cnblogs.com/aurora-ql/p/16514543.html)

如果在 onbeforeunload 与 onunload 内用 axios 发送请求，可能因为页面资源的快速变动导致请求中断。

解决方法如下：

- 可以手动对页面变动延时，让异步请求有足够时间发送，但不优雅。

- 等待 axios 的异步请求完毕后再继续执行，使用 async 和 await 模拟同步请求。（采用）

- 使用 fetch，并设置 keepAlive 参数为 true 。

	即使页面处于关闭状态也会保持连接，利用这个特性，可以发送可靠的请求。

- 使用 navigator.sendBeacon() 方法传输少量数据。

	该方法可用于通过HTTP将少量数据异步传输到Web服务器，使用户代理在有机会时异步地向服务器发送数据，同时不会延迟页面的卸载或影响下一导航的载入性能。（IE 不兼容）

### 打包（弃用）

#### 需求与方案

- 打包为 exe ，提供给 Windows 。

  用 exe4j 将 jar 打包成 exe文件。[exe4j 使用](https://juejin.cn/post/6937495228714582023)（建议在官网下载 exe4j）， [jdk 提取 jre](https://blog.csdn.net/code_peak/article/details/127132360)

- 打包为 apk ，提供给 Android 。

	需要建立 Android 应用，调整代码以适应 Android 平台，然后导出 apk 。

	- 这样才能自动打开关闭 Android 的浏览器。

#### 问题

对于 exe ：导出的 exe 某些功能不稳定，比如自动关闭后端这个功能。

对于 apk ：修改代码比较麻烦。

## PWA 版

在 Java 后端方案已经写好的前端的基础上修改。

### 同步方案

- 所有 Card 存储在一个 Json 文件中，文件名为 `datetime.json` 。
	- GitHub API 的每个请求响应限制为1 MB，超过需要分段下载。
- 存储所有 json 的历史版本。
- 每次进入 app 时进行云端同步。

## 开发进度



测试 token，60 天后（08 19）失效：

```txt

```



