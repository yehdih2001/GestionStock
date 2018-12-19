package com.management.repository;

import com.management.domain.ProductCount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ProductCount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductCountRepository extends JpaRepository<ProductCount, Long> {

}
