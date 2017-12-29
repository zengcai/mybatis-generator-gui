mybatis-generator-gui
==============

mybatis-generator-gui是基于[mybatis generator](http://www.mybatis.org/generator/index.html)开发一款界面工具, 本工具可以使你非常容易及快速生成Mybatis的Java POJO文件及数据库Mapping文件。

![MainUI](https://thumbnail0.baidupcs.com/thumbnail/34ff65ac4c34abaca1b5dd0e6491fa2e?fid=3859937792-250528-416692164134717&time=1514480400&rt=sh&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-ve%2Fu%2B0vor2ZDlOrYbd9xBqNEpp4%3D&expires=8h&chkv=0&chkbd=0&chkpc=&dp-logid=8397631741973068263&dp-callid=0&size=c710_u400&quality=100&vuk=-&ft=video)

### 说明
* useGenKey: insertSelective->record可获取自增主键
* selectOne: 根据查询条件只返回一条记录
* selectSelective: 查询只返回指定字段
* insert/update all: 插入/更新全部字段
* selectKeyForUpdate: 通过主键查询并锁定记录
* delete: delete方法
* count: count方法
* batchInsert: 批量插入

### 要求
本工具由于使用了Java 8的众多特性，所以要求 JRE或者JDK 8.0以上版本，对于JDK版本还没有升级的童鞋表示歉意。

### 启动本软件

* 方法一: 自助构建

```bash
    git clone https://github.com/astarring/mybatis-generator-gui
    cd mybatis-generator-gui
    mvn install:install-file -Dfile=./src/main/resources/lib/ojdbc14.jar -DgroupId=com.oracle -DartifactId=ojdbc14 -Dversion=10.2.0.3.0 -Dpackaging=jar -DgeneratePom=true
    mvn jfx:jar
    cd target/jfx/app/
    java -jar mybatis-generator-gui.jar
```
    
* 方法二: IDE中运行

Eclipse or IntelliJ IDEA中启动, 找到```com.zzg.mybatis.generator.MainUI```类并运行就可以了

    
增加了几个插件,部分来自：
https://github.com/itfsw/mybatis-generator-plugin
