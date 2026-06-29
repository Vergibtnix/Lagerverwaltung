package com.lagerverwaltung.backend.repository;

import com.lagerverwaltung.backend.domain.Produkt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProduktRepository extends JpaRepository<Produkt, Long> {

	List<Produkt> findAllByOrderByNameAsc();

	@Query("select distinct p.kategorie from Produkt p order by p.kategorie asc")
	List<String> findeAlleKategorienSortiert();

	Optional<Produkt> findByNameIgnoreCase(String name);
}

