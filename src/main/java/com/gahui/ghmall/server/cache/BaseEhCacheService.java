package com.gahui.ghmall.server.cache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;


import javax.annotation.Resource;


/**
 * @description: ehcache缓存基础实现
 * 本项目估计后期只使用单节点部署，故使用ehcache作为缓存
 * ehcache为java实现的进程内缓存
 * @author: Gahui
 * @since: 2021/2/3
 **/
public abstract class BaseEhCacheService {
    @Resource
    protected CacheManager cacheManager;

    /**
     * 获取
     */
    protected Element getCache(String cacheName, String key) {
        Ehcache ehcache = this.getEhcache(cacheName);
        if (ehcache != null) {
            return ehcache.get(key);
        }
        return null;
    }

    /**
     * 删除
     */
    protected boolean removeCache(String cacheName, String key) {
        Ehcache ehcache = this.getEhcache(cacheName);
        if (ehcache != null) {
            return ehcache.remove(key);
        }
        return false;
    }

    /**
     * 设置
     *
     * @param cacheName
     * @param key
     * @param value
     * @return
     */
    protected boolean setCache(String cacheName, String key, Object value) {
        Ehcache ehcache = this.getEhcache(cacheName);
        Element element = new Element(key, value);
        if (ehcache != null) {
            ehcache.put(element);
            return true;
        }
        return false;
    }

    /**
     * 不存在才设置
     */
    protected boolean setNxCache(String cacheName, String key, Object value) {
        Ehcache ehcache = this.getEhcache(cacheName);
        Element element = new Element(key, value);
        if (ehcache != null) {
            if (this.getCache(cacheName, key) != null) {
                return false;
            }
            ehcache.put(element);
            return true;
        }
        return false;
    }

    /**
     * 带有过期时间的设置
     */
    protected boolean setExCache(String cacheName, String key, Object value, int expireSeconds) {
        Ehcache ehcache = this.getEhcache(cacheName);
        Element element = new Element(key, value, 0, expireSeconds);
        if (ehcache != null) {
            ehcache.put(element);
            return true;
        }
        return false;
    }

    /**
     * 设置值，如有过期时间，不更新过期时间
     */
    protected void putCache(String cacheName, String key, Object value) {
        Ehcache ehcache = this.getEhcache(cacheName);
        Element element;
        if (ehcache != null) {
            element = ehcache.get(key);
            if (element == null) {
                element = new Element(key, value);
            } else {
                if (element.getTimeToLive() > 0 && element.getTimeToIdle() == 0) {
                    element = new Element(key, value, element.getVersion(), element.getCreationTime(), element.getLastAccessTime(),
                            element.getLastUpdateTime(), element.getHitCount());
                } else {
                    element = new Element(key, value);
                }
            }
            ehcache.put(element);
        }
    }

    /**
     * 根据缓存名称获取对应缓存
     */
    private Ehcache getEhcache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        EhCacheCache ehCacheCache = (EhCacheCache) cache;
        if (ehCacheCache != null) {
            return ehCacheCache.getNativeCache();
        }
        return null;
    }

}
