mybatis-generator-gui
==============

mybatis-generator-gui是基于[mybatis generator](http://www.mybatis.org/generator/index.html)开发一款界面工具, 本工具可以使你非常容易及快速生成Mybatis的Java POJO文件及数据库Mapping文件。

![MainUI](https://pan.baidu.com/s/1eRWnVSe#list/path=%2Fpublic%2Fimage)

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

    
增加了部分插件来自：
https://github.com/itfsw/mybatis-generator-plugin
