package ru.creditservices.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.creditservices.deal.model.entity.ClientEntity;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    @Query(value = "SELECT * FROM client " +
            "WHERE passport_id->>'series' = :series " +
            "AND passport_id->>'number' = :number", nativeQuery = true)
    Optional<ClientEntity> findClientEntityByPassportId(@Param("series") String series,
                                                         @Param("number") String number);

}
