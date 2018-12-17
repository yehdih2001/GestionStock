package com.management.repository.search;

import com.management.domain.Provider;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Provider entity.
 */
public interface ProviderSearchRepository extends ElasticsearchRepository<Provider, Long> {
}
