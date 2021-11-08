## 0x00、 当前网络上的相关库

0. https://github.com/search?q=android+permission
    - GitHub 检索内容
1. https://github.com/permissions-dispatcher/PermissionsDispatcher
    - 特性：注解
2. https://github.com/tbruyelle/RxPermissions
    - 特性：Rx
3. https://github.com/googlesamples/easypermissions
    - 特性：无特性


## 0x01、 借助于这个库的编写我们应该掌握的内容

1. 权限相关的细节(复习)
2. 结合注解设计一个库
3. 公共代码库的使用细节(复习)


## 0x02、 待解决的问题
1. 花了点时间但是仍然没找到 dangerous permission 的列表，记得之前在 Google 官方的文档上查到过，现在找不到不知为何。
2. 权限请求可否不在主线程中进行？
3. 关于多个权限一起请求的情况，如果只授予部分权限如何完成细节回调区分
4. 本来我们计划通过在 xml 文件中设置 flag 来标记是否进行过二次权限申请。
    但是问题在于国产定制手机厂商华为没有严格遵循 Google 的设计，直接弹出的权限拒绝页面就带有勾选框，
    所以现在看来我们解决此问题的最优的方式就是查看系统是把这个 flag 存在什么地方的，然后去同样的地方取出这个 falg 从而使这个判断更准确。
    - 最终解决方案是在权限被拒绝的时候将二次请求信息本地化，这样可以应对华为出现的问题
    
## 0x03、 跑通了 PermissionsDispatcher
- 我们需要探究这个库是如何分辨权限请求次数的
    - 在我们的项目中当前使用的方式是通过 XML 文件本地化标识符来记录特定权限的请求次数的。
    
## 0x04、 相关知识点

1. APT(Annotation Processing Tool)
2. KAPT(Kotlin Annotation Processing Tool)
2. java annotation knowledge
3. javaPoet

对于 Java 项目中的注解，我们可以通过反射调用目标类，然后获取使用了目标注解的类或者方法或者属性。
但是这一过程还可以通过 Android 开发框架变成另一种形式，某些部分对开发者来讲是透明的了。

## 0x05、 关于注解生成代码这件事的流程

1. 创建注解相关的类(需要学习注解技术相关语法)
2. 创建注解解析器
    - 注解解析器可以根据注解做任何想做的事情，这里我们要做的是生成若干代码
    - 注解解析器中有众多的文件和类，编译框架要想更快(避免遍历整个项目)的定位到注解解析器的入口文件 (Processor 接口的实现者)，
      需要开发者手动在 `META-INF/services/javax.annotation.processing.Processor` 对自己的入口文件进行注册，
      但是这无疑是一个容易被忘记的工作，于是相关的防止遗忘的工具诞生了 [com.google.auto.service:auto-service](https://github.com/google/auto/tree/master/service)
3. 通过 gradle 文件中的 annotationProcessor 关键字指定项目中的注解的解析工作交给谁来处理
    - 注解的解析发生在编译(生成 class 字节码)之前，所以我们编写的注解解析器是处理的是 .java 文件
    - 我们注解解析器 通过 我们的注解 找到我们关心的 类 或者 方法 然后生成我们希望生成的代码, 
      这个代码的生成过程当然可以手动(javaPoet工具可以避免完全手动生成代码)
    - 在编译期间生成的 .java 文件会和其他手动编写的 .java 文件一起被编译(先生成目标文件，然后一起被编译)
4. 当完成上述三个步骤中文件的编写工作后，并在自己的项目中使用了自定注解之后
    通过 Rebuild 项目即可生成我们希望生成的代码了。
5. 用代码生成的代码可以在 Rebuild 之后被正常调用。
6. **OVER**

## 0x06、 必需的 API
1. AbstractProcessor 解析自定义注解应该实现的接口
2. RoundEnvironment
3. javax.lang.model 此包中的内容用于抽象 Java 项目中的元素 (包，文件，类……)

## 0x07、 需要注意的点
1. 关于依赖 在 gradle 5.0 之后，如果要用 AutoService 需要两个语句
    -     implementation 'com.google.auto.service:auto-service:1.0-rc6'
    -     annotationProcessor 'com.google.auto.service:auto-service:1.0-rc6'
2. Kotlin 文件中使用注解和 java 文件中使用注解效果不同，有些差异需要进一步区分
    - You should use java , if this your first do the operate.

### 7.1 Kotlin 中使用注解和 Java 中使用注解存在什么差异以及如何化解
gradle 中使用 kapt 替换 annotationProcessor。
-  kapt 扩展了 annotationProcessor，同时也支持对 kotlin 文件中注解的处理
- 要使用 kapt 还需增加这个插件： `apply plugin: 'kotlin-kapt'` 

## 0x08、 编译期调试
- https://blog.csdn.net/stupid56862/article/details/82224957
    

## 0x09、 中期小结
至此我们完成了什么
1. 了解了注解是什么(相关语法和类)，以及其用法
2. 了解了注解解析器是什么以及它的用法
3. 了解了AndroidStudio如何使用和调试注解解析器
4. 通过 Demo 体验了编译期代码生成过程的整个流程
5. 对这一过程做了概要记录

还需要做什么
1. 生成真正有意义的代码——这需要进一步了解之前用到的 API 的更多细节和使用方式
2. 生成一个详细的学习文章

说真的我并不觉得通过注解的方式把权限请求的代码分散书写的形式是一种好的形式，个人更喜欢链式调用。

## 0x0 Reference 
- [Java官方文档有对于一切内容最有力的描述](https://docs.oracle.com/javase/tutorial/java/annotations/index.html)
    - 它是英文的，我暂时还没有通读它，后来读了，没卵用[尴尬脸]
- 《Java编程思想》20章 对注解技术有详细且有力的介绍
- https://blog.csdn.net/xx326664162/article/details/68490059
- https://juejin.im/entry/5b2382d851882574bf63efc0
- [编译期 debug](https://www.jianshu.com/p/4fef2ad51f5a)
    - @AutoService 作用描述
- [Android APT 自动生成代码实践](https://blog.csdn.net/u010976213/article/details/91999309)
- 注解自动生成代码过程中的调试方法
    - https://blog.csdn.net/stupid56862/article/details/82224957
    - https://www.jianshu.com/p/cc369dca20d1
    
   