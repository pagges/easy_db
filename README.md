# EasyDB

## 关于
基于 Bitcask 存储模型的思想设计出来的 K-V 存储，满足日常简单的使用场景

### 一些问题

#### 1. 为什么要自己手写 K-V 存储？

服务端程序开发，其实本质就是对数据的操作，具体的说就是数据的存和取。日常开发中数据库帮助我们屏蔽了数据和操作系统的交互，
我们使用数据库提供的驱动进行交互完成数据的存取操作，至于数据库怎么和操作系统的交互，我们选择性的忽略了。
往往在线上服务遇到相关问题的时候才会去选择性的了解部分底层特性，
希望能够解决燃眉之急(这往往会花费大部分的时间，且不一定能够解决当下的问题，
更多的时候是看了些底层设计，然后结合自己的服务自身情况推理出问题可能出现的方向，然后做些尝试)，
这个过程构建起来知识不成体系，往往只会局限在问题的冰上一角。要是我们自己体验一下设计一个简单的存储，
了解数据库设计的各个环节在思路和解决什么问题，那么这会对问题的排查有本质上的帮助。

#### 2. 为什么是 bitcask?

在评估存储引擎时，我们寻求许多目标，包括：
- 读写低延迟
- 高吞吐，尤其在写入随机流的时候
- 可以写超过RAM大小的数据
- 不怕崩溃，崩溃后可以快速恢复，不丢数据
- 易于备份和还原
  
以上特性 bitcask 模型都有，可以说是麻雀虽小五脏俱全了，加上其模型原理相对简单，编码门槛比较低，特别适合新手尝试着自己动手实现一个 K-V 存储。
## 项目的节点划分

作为一个学习的过程而言最好的方式是由简入繁，不要求自己一口吃成一个胖子，如果当下的目标是实现「分布式 K-V 存储」，这对于目前的我来说是不能实现的目标，所以我倾向于将目标拆解，逐步精进。
可以说 EasyDB 的方向可能会往 「分布式 K-V」 存储发展。所以我认为不同阶段的边界是不同的，我大致会划分为下面几个阶段，每个阶段所要着重的技术点也是不同的。

**3.1 可用就行**

这个阶段主要是能够基于 bitcask 存储模型的思想设计一个能够实现 put、set 的 K-V 存储，至于文件是以怎样的方式存储和读取，都无所谓，性能不做要求，数据都是通过文件交互的，没有内存的部分。

这个阶段其实也是培养兴趣的过程，如果目标达到将会有更大的动力继续推进，加上一定的反馈机制会更好的和自己身体里的懒虫斗争。

主要任务:
1. 初步理解 bitcask 模型，针对当下的目标设计存储的 Entry 结构，实现 K-V 数据的存储和读取。
2. 主要会聚焦与文件操作，探索更加合理的序列化方式。

**3.2 内存索引的设计**

上一个阶段我们对 bitcask 存储模型进行了初步的探索，可能会暴露出各种问题，所以这个阶段首先是要求解这些问题，形成更加成熟的实现方案，再次基础上我们的数据都是存储在文件中的，
读取文件的时候缺乏寻址能力，至少我们需要维护一份 Key-Index 的映射表在文件系统的其他地方或者是在内存中。以便于我们精确的知道某个 Key 存储在文件系统的什么位置。

主要任务：
1. 解决上一个阶段暴露出来的问题，可能是设计上的，也可能是文件存储上的。
2. 维护一份 Key-Index 在内存中，实现精确的查询数据，其结构如下所示

![image](https://github.com/pagges/easy_db/tree/master/doc/img/easy_db.png)


**3.3 删除、合并操作**

前面的阶段我们对 bitcask 模型的读写部分有感觉了，目前实现了 put、get 操作，接下来需要实现 delete 操作，
这里单独区分开 delete 是因为，delete 操作需要结合文件的合并实现，因为 bitcask 模型的删除其实是追加写入带有删除标记的记录，
在读取数据时需要做策略上的调整，且标记了删除的数据可以被认定为冗余的数据。可以通过记录合并的方式清理部分冗余的数据，达到节省磁盘的作用。
同时有了删除操作还需要考虑更新内存 Key-Index 映射表的策略，整体而言这部分是新的挑战。

主要任务:
1. 数据删除，设计合理的数据读取策略，跳过被标识为删除的数据，且更新内存中的 Key-Index 映射表。
2. 数据合并，清理冗余的数据，同时更新内存中的 Key-Index 映射表。


**3.4 寻找工程优化点**

实现到这个阶段想比我们的 K-V 存储已经基本可用(对性能无要求)，也对 bitcask 模型有了较为深切的理解，但是在工程层面我们存在很多的优化空间，
考虑到前面的阶段都是追求可用为主，所以设计上可能会忽略一些关键的地方，这里也是初步拟定下面的优化点：

1. 文件存储的方式，大文件拆分成小文件，内存中维护的 Key-Index 在每次启动的时候都需要读取文件来构建，如果一次读取的文件过大，会影响初始化过程。
2. 数据清理线程的优化。
