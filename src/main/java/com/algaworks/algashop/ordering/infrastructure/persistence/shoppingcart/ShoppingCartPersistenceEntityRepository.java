package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartPersistenceEntityRepository extends JpaRepository<ShoppingCartPersistenceEntity, UUID> {

    Optional<ShoppingCartPersistenceEntity> findByCustomer_Id(final UUID customerId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE ShoppingCartItemPersistenceEntity i SET
               i.price = :price,
               i.totalAmount = :price * i.quantity
        WHERE
               i.productId = :productId
        """)
    void updateItemPrice(@Param("productId") final UUID productId, @Param("price") final BigDecimal price);

    @Modifying
    @Transactional
    @Query("""
        UPDATE ShoppingCartItemPersistenceEntity i SET
               i.available = :available
        WHERE
               i.productId = :productId
        """)
    void updateItemAvailability(@Param("productId")final UUID productId, @Param("available") final boolean availability);

    @Modifying
    @Transactional
    @Query("""
        UPDATE ShoppingCartPersistenceEntity s SET
               s.totalAmount = (SELECT SUM(i.totalAmount)
                                  FROM ShoppingCartItemPersistenceEntity i
                                 WHERE i.shoppingCart.id = s.id)
        WHERE EXISTS (SELECT 1
                        FROM ShoppingCartItemPersistenceEntity i2
                       WHERE i2.shoppingCart.id = s.id
                         AND i2.productId = :productId)
        """)
    void recalculateTotalsForCartsWithProduct(@Param("productId")final UUID productId);

    @Override
    @EntityGraph(attributePaths = {"customer", "items"})
    Optional<ShoppingCartPersistenceEntity> findById(final UUID id);

    @Query("""
    SELECT s
    FROM ShoppingCartPersistenceEntity s
    LEFT JOIN FETCH s.items
    WHERE s.customer.id = :customerId
    """)
    Optional<ShoppingCartPersistenceEntity> findByCustomerId(@Param("customerId") final UUID id);

}
