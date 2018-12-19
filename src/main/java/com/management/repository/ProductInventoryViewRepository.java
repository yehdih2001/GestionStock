package com.management.repository;

import com.management.domain.ProductInventoryView;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ProductInventoryView entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductInventoryViewRepository extends JpaRepository<ProductInventoryView, Long> {

}
