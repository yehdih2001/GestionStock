package com.management.config;

import java.time.Duration;

import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;

import io.github.jhipster.config.jcache.BeanClassLoaderAwareJCacheRegionFactory;
import io.github.jhipster.config.JHipsterProperties;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.*;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        BeanClassLoaderAwareJCacheRegionFactory.setBeanClassLoader(this.getClass().getClassLoader());
        JHipsterProperties.Cache.Ehcache ehcache =
            jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(com.management.domain.Client.class.getName(), jcacheConfiguration);
            cm.createCache(com.management.domain.Client.class.getName() + ".prodcuts", jcacheConfiguration);
            cm.createCache(com.management.domain.Provider.class.getName(), jcacheConfiguration);
            cm.createCache(com.management.domain.Provider.class.getName() + ".prodcuts", jcacheConfiguration);
            cm.createCache(com.management.domain.Product.class.getName(), jcacheConfiguration);
            cm.createCache(com.management.domain.Product.class.getName() + ".providers", jcacheConfiguration);
            cm.createCache(com.management.domain.Product.class.getName() + ".clients", jcacheConfiguration);
            cm.createCache(com.management.domain.Compagny.class.getName(), jcacheConfiguration);
            cm.createCache(com.management.domain.AppUser.class.getName(), jcacheConfiguration);
            cm.createCache(com.management.domain.Preference.class.getName(), jcacheConfiguration);
            cm.createCache(com.management.domain.Erp.class.getName(), jcacheConfiguration);
            cm.createCache(com.management.domain.Erp.class.getName() + ".compagnies", jcacheConfiguration);
            cm.createCache(com.management.domain.Erp.class.getName() + ".users", jcacheConfiguration);
            cm.createCache(com.management.domain.ProductInventoryView.class.getName(), jcacheConfiguration);
            cm.createCache(com.management.domain.ProductReodringRules.class.getName(), jcacheConfiguration);
            cm.createCache(com.management.domain.ProductCount.class.getName(), jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
        };
    }
}
