package com.management.repository.search;

import com.management.domain.ProductCount;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ProductCount entity.
 */
public interface ProductCountSearchRepository extends ElasticsearchRepository<ProductCount, Long> {
}
