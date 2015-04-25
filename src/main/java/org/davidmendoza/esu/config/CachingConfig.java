/*
 * The MIT License
 *
 * Copyright 2014 Southwestern Adventist University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.davidmendoza.esu.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
        CacheConfiguration roles = new CacheConfiguration();
        roles.setName("org.davidmendoza.esu.model.Rol");

        CacheConfiguration usuarios = new CacheConfiguration();
        usuarios.setName("org.davidmendoza.esu.model.Usuario");

        CacheConfiguration trimestres = new CacheConfiguration();
        trimestres.setName("org.davidmendoza.esu.model.Trimestre");

        CacheConfiguration publicaciones = new CacheConfiguration();
        publicaciones.setName("org.davidmendoza.esu.model.Publicacion");

        CacheConfiguration articulos = new CacheConfiguration();
        articulos.setName("org.davidmendoza.esu.model.Articulo");

        CacheConfiguration diaCache = new CacheConfiguration();
        diaCache.setName("diaCache");
        diaCache.setTimeToLiveSeconds(3600);
        diaCache.setTimeToIdleSeconds(3600);

        CacheConfiguration inicioCache = new CacheConfiguration();
        inicioCache.setName("inicioCache");
        inicioCache.setTimeToLiveSeconds(3600);
        inicioCache.setTimeToIdleSeconds(3600);

        CacheConfiguration equipoCache = new CacheConfiguration();
        equipoCache.setName("equipoCache");
        equipoCache.setTimeToLiveSeconds(3600);
        equipoCache.setTimeToIdleSeconds(3600);

        CacheConfiguration defaultConfig = new CacheConfiguration();
        defaultConfig.setEternal(false);
        defaultConfig.setTimeToLiveSeconds(21600);
        defaultConfig.setTimeToIdleSeconds(21600);
        defaultConfig.setMemoryStoreEvictionPolicy("LRU");
        defaultConfig.setOverflowToOffHeap(false);
        
        SizeOfPolicyConfiguration sizeOfPolicy = new SizeOfPolicyConfiguration();
        sizeOfPolicy.setMaxDepth(20000);
        sizeOfPolicy.setMaxDepthExceededBehavior("abort");

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.setName("esuCache");
        config.setMaxBytesLocalHeap("100M");
        config.addSizeOfPolicy(sizeOfPolicy);
        config.addDefaultCache(defaultConfig);
        config.addCache(roles);
        config.addCache(usuarios);
        config.addCache(trimestres);
        config.addCache(publicaciones);
        config.addCache(articulos);
        config.addCache(diaCache);
        config.addCache(inicioCache);
        config.addCache(equipoCache);

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }
}
