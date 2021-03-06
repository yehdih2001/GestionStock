package com.management.repository;

import com.management.domain.Erp;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Erp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ErpRepository extends JpaRepository<Erp, Long> {

}
