package net.eai.util.memcached;

//关于memcache的参数设置
public class MemCacheConstants 
{
   //缓存服务器地址,多个服务器的话使用“,”区分
   public static String memservers = "";
   
   //设置初始连接数
   public static int initconn = 5;
   
   //设置最小连接数
   public static int minconn = 5;
   
   //设置最大连接250
   public static int maxconn = 250;
   
   //每个链接的最大空闲时间 默认3小时
   public static long maxidletime = 1000 * 60 * 60 * 3;
   
}
