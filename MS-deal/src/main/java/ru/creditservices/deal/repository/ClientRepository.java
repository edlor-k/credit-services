package ru.creditservices.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.creditservices.deal.model.entity.ClientEntity;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByPassportId_SeriesAndPassportId_Number(String series, String number);

}
