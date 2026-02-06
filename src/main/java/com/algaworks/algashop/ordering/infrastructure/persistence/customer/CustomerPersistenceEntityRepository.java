package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerPersistenceEntityRepository extends JpaRepository<CustomerPersistenceEntity, UUID> {

    Optional<CustomerPersistenceEntity> findByEmail(final String email);

    boolean existsByEmailAndIdNot(final String email, final UUID id);

}
