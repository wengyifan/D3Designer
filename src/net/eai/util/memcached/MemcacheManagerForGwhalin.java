package net.eai.util.memcached;

import java.util.Date;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcacheManagerForGwhalin {
	
	// 构建缓存客户端
	private static MemCachedClient cachedClient;
	// 单例模式实现客户端管理类
	private static MemcacheManagerForGwhalin INSTANCE = new MemcacheManagerForGwhalin();

	private MemcacheManagerForGwhalin() {
		cachedClient = new MemCachedClient();
		//获取连接池实例
		SockIOPool pool = SockIOPool.getInstance();

		//设置缓存服务器地址，可以设置多个实现分布式缓存
		String[] servers = MemCacheConstants.memservers.split(",");
		pool.setServers(servers);
		
		//设置初始连接5
		pool.setInitConn(MemCacheConstants.initconn);
		//设置最小连接5
		pool.setMinConn(MemCacheConstants.minconn);
		//设置最大连接250
		pool.setMaxConn(MemCacheConstants.maxconn);
		//设置每个连接最大空闲时间3个小时
		pool.setMaxIdle(MemCacheConstants.maxidletime);

		// 设置主线程的睡眠时间 
		pool.setMaintSleep(30);

		// 设置TCP的参数，连接超时等 
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);
		pool.initialize();

	}
	
	/**
	 * 获取缓存管理器唯一实例
	 * @return
	 */
	public static MemcacheManagerForGwhalin getInstance() {
		return INSTANCE;
	}

//	public void add(String key, Object value) {
	//	cachedClient.set(key, value,new Date(System.currentTimeMillis() + Integer.valueOf(60000 * 10)));
//	}

	//带超时的key 输入格式如：add(key,value,new Date(5000))
	public void add(String key, Object value, int milliseconds) {
		//cachedClient.set(key, value, milliseconds);
		cachedClient.set(key, value, new Date(System.currentTimeMillis() + milliseconds));
	}

	public void remove(String key) {
		cachedClient.delete(key);
	}

	public void remove(String key, int milliseconds) {
		cachedClient.delete(key, milliseconds, new Date());
	}

	public void update(String key, Object value, int milliseconds) {
		cachedClient.replace(key, value, milliseconds);
	}

	public void update(String key, Object value) {
		cachedClient.replace(key, value);
	}
	
	public Object get(String key) {
		return cachedClient.get(key);
	}
	
	//数组形式的获取
	public Object[] getByMuti(String[] keys)
	{
		return cachedClient.getMultiArray(keys);
	}
 
}
