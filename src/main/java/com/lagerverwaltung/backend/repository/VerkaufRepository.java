package com.lagerverwaltung.backend.repository;

import com.lagerverwaltung.backend.domain.Verkauf;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VerkaufRepository extends JpaRepository<Verkauf, Long> {

	@EntityGraph(attributePaths = "produkt")
	List<Verkauf> findAllByOrderByVerkauftAmDesc();

	@EntityGraph(attributePaths = "produkt")
	Optional<Verkauf> findOneById(Long id);

	boolean existsByProduktId(Long produktId);

	@Query("select coalesce(sum(v.gesamtpreis), 0) from Verkauf v")
	BigDecimal berechneUmsatzGesamt();
}

