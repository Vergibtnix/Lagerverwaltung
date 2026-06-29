package com.lagerverwaltung.backend.repository;

import com.lagerverwaltung.backend.domain.NachbestellStatus;
import com.lagerverwaltung.backend.domain.Nachbestellung;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NachbestellungRepository extends JpaRepository<Nachbestellung, Long> {

	@EntityGraph(attributePaths = "produkt")
	List<Nachbestellung> findAllByOrderByBestelltAmDesc();

	@EntityGraph(attributePaths = "produkt")
	List<Nachbestellung> findByStatusOrderByBestelltAmDesc(NachbestellStatus status);

	@EntityGraph(attributePaths = "produkt")
	Optional<Nachbestellung> findOneById(Long id);

	boolean existsByProduktId(Long produktId);
}

