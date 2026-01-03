package com.algaworks.algashop.ordering.infrastruct.persistence.repository;

import com.algaworks.algashop.ordering.infrastruct.persistence.entity.OrderPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPersistenceEntityRepository extends JpaRepository<OrderPersistenceEntity, Long> {
}
