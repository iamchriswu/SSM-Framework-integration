# SSM框架整合详解
## 1.需求分析
> 实现简单登录注册

## 2.工程环境
* JDK1.8
* Tomcat8.0
* spring4.2.4RELEASE
* mybatis3.2.8
* jar包截图
> ![jars](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/jars.png)

## 3.mysql建立user表
### 如下图
> ![mysql](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/mysql.png)

## 4.创建一个web工程
### 工程目录结构
> ![project](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/project.png)

## 5.配置resources里的配置文件
### 5.1 jdbc.properties mysql连接池的属性配置(xxxx的地方每个人都不一样)
```Java
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://xxxx:3306/xxxx?characterEncoding=utf-8
jdbc.username=xxxx
jdbc.password=xxxx
```

### 5.2 log4j.properties 日志配置
```Java
#定义LOG输出级别，开发时推荐用DEBUG，看错误信息
log4j.rootLogger=DEBUG,Console,File
#定义日志输出目的地为控制台
log4j.appender.Console=org.apache.log4j.ConsoleAppender
log4j.appender.Console.Target=System.out
#可以灵活地指定日志输出格式，下面一行是指定具体的格式
log4j.appender.Console.layout = org.apache.log4j.PatternLayout
log4j.appender.Console.layout.ConversionPattern=[%c] - %m%n

#文件大小到达指定尺寸的时候产生一个新的文件
log4j.appender.File = org.apache.log4j.RollingFileAppender
#指定输出目录
log4j.appender.File.File = logs/ssm.log
#定义文件最大大小
log4j.appender.File.MaxFileSize = 10MB
# 输出所以日志，如果换成DEBUG表示输出DEBUG以上级别日志
log4j.appender.File.Threshold = ALL
log4j.appender.File.layout = org.apache.log4j.PatternLayout
log4j.appender.File.layout.ConversionPattern =[%p] [%d{yyyy-MM-dd HH\:mm\:ss}][%c]%m%n
```

### 5.3 sqlMapConfig.xml mybatis配置文件
> 这里mybatis和spring进行整合了，这里面只做了别名配置，其他的配置在spring的配置文件中
```Java
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>
    <!-- 别名配置 -->
    <typeAliases>
        <package name="com.chris.pojo"/>
    </typeAliases>

</configuration>
```
### 5.4 springmvc.xml springmvc的配置
> 由于springmvc时spring的一部分，无需与spring进行整合，只需要配置自己的
```Java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
						http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.2.xsd
						http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.2.xsd">

    <!-- 配置扫描注解  @Controller @Service -->
    <context:component-scan base-package="com.chris.controller" />

    <!-- SpringMVC使用<mvc:annotation-driven>自动加载RequestMappingHandlerMapping和RequestMappingHandlerAdapter -->
    <mvc:annotation-driven />

    <!-- 配置视图解析器-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <!-- 配置逻辑视图的前缀 -->
        <property name="prefix" value="" />
        <!-- 配置逻辑视图的后缀 -->
        <property name="suffix" value=".jsp" />
    </bean>

</beans>
```

### 5.5 applicationContext.xml spring的配置文件
> 这里用到的是阿里巴巴的druid连接池，顺带也把service层的一个实现类也配置成JavaBean了
```Java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 加载数据库连接信息配置文件 -->
    <context:property-placeholder location="classpath:jdbc.properties" />

    <!-- druid连接池 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driver}" />
        <property name="url" value="${jdbc.url}" />
        <property name="username" value="${jdbc.username}" />
        <property name="password" value="${jdbc.password}" />
    </bean>

    <!-- 配置Mybatis的sqlSessionFactory -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="configLocation" value="classpath:sqlMapConfig.xml"/>
    </bean>

    <!-- Mapper动态代理开发  扫包  给定包下的接口文件名和映射文件名必须相同  创建接口的实现类-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="com.chris.mapper" />
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
    </bean>

    <!-- 开启事务 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource" />
    </bean>
    <!-- 事务注解驱动-->
    <tx:annotation-driven />

    <!-- service -->
    <bean id="userService" class="com.chris.service.impl.UserServiceImpl" />

</beans>
```

### 5.6 generatorConfig.xml mybatis逆向工程的配置文件
> 这里通过mybatis提供的方法，根据数据库中的表实现自动生成实体类pojo以及dao层的mapper.java和mapper.xml。生成的方法有很多种，我是用maven的插件生成的。
```Java
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd" >
<generatorConfiguration >
    <!-- mysql jar 文件位置 -->
    <classPathEntry location="C:\\Tools\\maven_local_repository\\mysql\\mysql-connector-java\\5.1.32\\mysql-connector-java-5.1.32.jar" />
    <context id="store" targetRuntime="MyBatis3">
        <commentGenerator>
            <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
            <property name="suppressAllComments" value="true" />
            <!-- 是否去除所有自动生成的文件的时间戳，默认为false -->
            <property name="suppressDate" value="true"/>
        </commentGenerator>
        <!--数据库连接的信息：驱动类、连接地址、用户名、密码 -->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://xxxx:3306/xxxx"
                        userId="xxxx"
                        password="xxxx">
        </jdbcConnection>
        <!-- targetPackage:包名称(自定义)  targetProject：项目路径(自定义)   -->
        <!--定义model的包名称-->
        <javaModelGenerator targetPackage="com.chris.pojo" targetProject="./src">
            <!-- enableSubPackages:是否让schema作为包的后缀 -->
            <property name="enableSubPackages" value="false" />
            <!-- 从数据库返回的值被清理前后的空格  -->
            <property name="trimStrings" value="true" />
        </javaModelGenerator>

        <!-- 配置生成相应的实体Mapper.xml，对于Mapper3.X我们需要把type="XMLMAPPER" -->
        <!-- targetPackage:包名称(自定义)  targetProject：项目路径(自定义)   -->
        <sqlMapGenerator targetPackage="com.chris.mapper" targetProject="./src">
            <property name="enableSubPackages" value="false" />
        </sqlMapGenerator>

        <!-- 配置生成相应的接口类，对应与Mapper.xml中的一系列CRUD方法SQL语句 -->
        <!-- targetPackage:包名称(自定义)  targetProject：项目路径(自定义)   -->
        <javaClientGenerator targetPackage="com.chris.mapper" targetProject="./src" type="XMLMAPPER">
            <property name="enableSubPackages" value="false" />
        </javaClientGenerator>

        <!-- 所需生成的表配置 -->
        <table tableName="user" />
        <table tableName="file" />
        <table tableName="user_file" />

    </context>
</generatorConfiguration>
```

### 5.7 逆向工程生成后的详细目录
#### 这里本人自己的项目有三张表，这里只需一张user表即可
> ![project](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/projectDetails.png)

## 6.web.xml的配置
```Java
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>

    <!-- spring监听器 -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- 指定spring核心配置文件 -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:applicationContext.xml</param-value>
    </context-param>

    <!-- 前端控制器 -->
    <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:springmvc.xml</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <url-pattern>*.action</url-pattern>
    </servlet-mapping>

    <!-- Post请求乱码过滤器 -->
    <filter>
        <filter-name>characterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>characterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

</web-app>
```

## 7.持久层(mapper)的开发
> 这里演示自动生成的mapper文件（注册）及手动编写mapper文件（登录）自动生成的代码就不演示了，这里看手动编写的mapper
### UserMapperCustom.java
```Java
package com.chris.mapper;

import com.chris.pojo.UserCustom;
import com.chris.pojo.UserQueryVo;

public interface UserMapperCustom {
    UserCustom selectUserByUserNameAndPwd(UserQueryVo userQueryVo);
}
```
### UserMapperCustom.xml
```Java
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.chris.mapper.UserMapperCustom" >
  
    <!-- 动态sql片段 -->
    <sql id="query_user_where">
        <if test="userCustom != null">
            <if test="userCustom.userName != null and userCustom.password != null">
                user_name = '${userCustom.userName}' and password = '${userCustom.password}'
            </if>
        </if>
    </sql>
    
    <!-- 输入类型和输出类型分别是自定义的User包装类UserQueryVo和User的扩展类UserCustom -->
    <select id="selectUserByUserNameAndPwd" parameterType="com.chris.pojo.UserQueryVo" resultType="com.chris.pojo.UserCustom">
        select * from user
        <where>
            <include refid="query_user_where" />
        </where>
    </select>

</mapper>
```
> 这里解释一下自定义的User包装类UserQueryVo和User的扩展类UserCustom
### UserQueryVo.java
> User包装类用于一些复杂查询条件的存储，可以直接包装在一个对象中，mapper.xml中使用这种对象输入非常方便
```Java
package com.chris.pojo;

public class UserQueryVo {
    //用户信息
    private User user;

    //用户信息扩展类
    private UserCustom userCustom;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserCustom getUserCustom() {
        return userCustom;
    }

    public void setUserCustom(UserCustom userCustom) {
        this.userCustom = userCustom;
    }
}
```
### UserCustom.java
> UserCustom类继承User类，后面可能表会改动，不会再动User.java.直接动User的扩展类UserCustom.java即可。这样子方便表的扩展
```Java
package com.chris.pojo;

public class UserCustom extends User {

}
```

## 8.业务逻辑层(service)的开发
> 写UserService的接口及实现类，然后在把UserService在spring容器中注册即可
### UserService.java
```Java
package com.chris.service;

import com.chris.pojo.User;
import com.chris.pojo.UserCustom;
import com.chris.pojo.UserQueryVo;

public interface UserService {
    public int insertUser(User user) throws Exception;
    public UserCustom findUserByUserNameAndPassword(UserQueryVo userQueryVo) throws Exception;
}
```
### UserServiceImpl.java
```Java
package com.chris.service.impl;

import com.chris.mapper.UserMapper;
import com.chris.mapper.UserMapperCustom;
import com.chris.pojo.User;
import com.chris.pojo.UserCustom;
import com.chris.pojo.UserQueryVo;
import com.chris.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class UserServiceImpl implements UserService {
    //mapper的注入
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMapperCustom userMapperCustom;

    @Override
    public int insertUser(User user) throws Exception {
        user.setTime(new Date());
        return userMapper.insert(user);
    }

    @Override
    public UserCustom findUserByUserNameAndPassword(UserQueryVo userQueryVo) throws Exception {
        return userMapperCustom.selectUserByUserNameAndPwd(userQueryVo);
    }
}
```

## 9.视图层(controller)的开发
> 这里使用注解的方式进行配置的，@Controller是指这是一个controller的类，@RequestMapping是方法对应的action的配置，类似servlet。
### UserController.java
```Java
package com.chris.controller;

import com.chris.pojo.User;
import com.chris.pojo.UserCustom;
import com.chris.pojo.UserQueryVo;
import com.chris.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    public String register(User user, Model model) throws Exception{
        if (userService.insertUser(user) == 1) {
            model.addAttribute("info","success");
        } else {
            model.addAttribute("info", "error");
        }
        return "message";
    }

    @RequestMapping("/login")
    public String login(String userName, String password, Model model) throws Exception {
        //构建User的包装对象
        System.out.println(userName + " " + password);
        UserCustom userCustom = new UserCustom();
        userCustom.setUserName(userName);
        userCustom.setPassword(password);
        UserQueryVo userQueryVo = new UserQueryVo();
        userQueryVo.setUserCustom(userCustom);

        if (userService.findUserByUserNameAndPassword(userQueryVo) != null) {
            model.addAttribute("info","success");
        } else {
            model.addAttribute("info", "error");
        }
        return "message";
    }
}
```

## 10.Jsp页面的开发
> 这里主要测试整个项目，页面比较简单
### login.jsp
```Java
<%--
  Created by IntelliJ IDEA.
  User: Chris
  Date: 2018/9/7
  Time: 15:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>用户登录</title>
</head>
<body>
<form action="${pageContext.request.contextPath}login.action">
    <table border="1">
        <tr>
            <td>用户名</td>
            <td><input type="text" name="userName"></td>
        </tr>
        <tr>
            <td>密码</td>
            <td><input type="text" name="password"></td>
        </tr>
        <tr>
            <td><input type="submit" value="登录"></td>
        </tr>
    </table>
</form>
</body>
</html>
```
### register.jsp
```Java
<%--
  Created by IntelliJ IDEA.
  User: Chris
  Date: 2018/9/7
  Time: 15:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户注册</title>
</head>
<body>
<form action="${pageContext.request.contextPath}register.action">
    <table border="1">
        <tr>
            <td>用户名</td>
            <td><input type="text" name="userName"></td>
        </tr>
        <tr>
            <td>密码</td>
            <td><input type="text" name="password"></td>
        </tr>
        <tr>
            <td><input type="submit" value="注册"></td>
        </tr>
    </table>
</form>
</body>
</html>
```
### success.jsp
```Java
<%--
  Created by IntelliJ IDEA.
  User: Chris
  Date: 2018/10/4
  Time: 13:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
${info}
</body>
</html>
```

## 11.测试结果
> ![project](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/1.png)
> ![project](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/2.png)
> ![project](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/3.png)
> ![project](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/4.png)
> ![project](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/5.png)
> ![project](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/2.png)
> ![project](https://github.com/iamchriswu/SSM-Framework-integration/blob/master/images/6.png)
### 至此，整个SSM框架整合完毕。如若有什么不懂想请教或者整合有什么问题的话，欢迎联系我。
> 联系方式：3121987131@qq.com
