# SpringBoot-jwt-shiro-mybatis-redis
使用SpringBoot+jwt+shiro+MyBatis+redis实现了一个简单的用户登录以及权限控制
我也是一个很白很白的小白，项目中还有很多的不足，希望各位大佬指点 Email:499699859@qq.com

前些天学习分布式时候，发现传统登录根本无法满足需求，因为在传统项目中大多数登录都是将登录信息保存在session中，但是在分布式之中，session共享已经是一个问题，显然这个时候使用传统的登录已经无法满足需求，在网上扒了很多文章，发现了可以采用jwt+shiro方式进行登录，就满足了分布式中登录的需求，这里jwt和shiro就不过多介绍，有兴趣的可以百度一下

项目中代码注释很全面，有兴趣的小伙伴可以看看，希望能够帮到大家，同样的，项目中有很多地方不足，希望大家指出

话不多说，直接上运行图：
![image](https://github.com/HappyBugs/SpringBoot-jwt-shiro-mybatis-redis/blob/master/imagse/oneLogin.png)
 
第一次我们使用了一个 权限为view,edit的用户，可以看到生成的token信息为:eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJpZFwiOjEsXCJ1c2VyTmFtZVwiOlwi5L2g5aW9XCJ9IiwiZXhwIjoxNTU2MzU0Mzc2fQ.WmUnHIz7me70V1AEi-z6Fogykb9aNj4InsIbsZxU75A

![image](https://github.com/HappyBugs/SpringBoot-jwt-shiro-mybatis-redis/blob/master/imagse/towLogin.png)
我们使用同样的账号再次登录，发现并没有生成新的token 而是继续返回的上次的token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJpZFwiOjEsXCJ1c2VyTmFtZVwiOlwi5L2g5aW9XCJ9IiwiZXhwIjoxNTU2MzU0Mzc2fQ.WmUnHIz7me70V1AEi-z6Fogykb9aNj4InsIbsZxU75A

这样我们就可以看出，在我们设置的有效期内，用户已经是成功的登录，并且不会重复进行登录。

![image](https://github.com/HappyBugs/SpringBoot-jwt-shiro-mybatis-redis/blob/master/imagse/denglu.png)
然后我们查看是否登录，这个时候我们只需要在header中添加字段Authorization：刚生产的token信息
这个时候shiro就会给我们处理用户信息，判断时候在http请求头中存在token并添加用户信息，以及权限分配


![image](https://github.com/HappyBugs/SpringBoot-jwt-shiro-mybatis-redis/blob/master/imagse/admin.png)
接下来我们测试权限问题，首先我们使用admin角色进行登录，访问只有admin角色才可以访问的地址，结果是成功的，我们可以访问

![image](https://github.com/HappyBugs/SpringBoot-jwt-shiro-mybatis-redis/blob/master/imagse/noAdmin.png)
接下来，我们使用不是admin的角色进行访问，返现访问是不成功的，报错了，原因是当前角色没有admin权限，如上图

我们就实现了一个简单的使用jwt+shiro进行登录已经权限控制

当然项目还有很多不足，比如说使用shiro-redis进行缓存用户信息还没有实现，项目中有很多方法被注释了的，那就是shiro-redis的一相关的代码

