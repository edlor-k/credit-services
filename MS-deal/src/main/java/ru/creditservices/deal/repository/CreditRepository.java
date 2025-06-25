package ru.creditservices.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.creditservices.deal.model.entity.CreditEntity;

import java.util.UUID;

public interface CreditRepository extends JpaRepository<CreditEntity, UUID> {
}
