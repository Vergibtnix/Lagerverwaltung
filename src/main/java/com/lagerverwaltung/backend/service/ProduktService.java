package com.lagerverwaltung.backend.service;

import com.lagerverwaltung.backend.domain.Produkt;
import com.lagerverwaltung.backend.repository.NachbestellungRepository;
import com.lagerverwaltung.backend.repository.ProduktRepository;
import com.lagerverwaltung.backend.repository.VerkaufRepository;
import com.lagerverwaltung.backend.web.form.ProduktForm;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProduktService {

	private static final Map<String, String> BILD_MAPPING = Map.of(
		"ktm 390 adventure", "/img/KTM%20390%20Adventure.webp",
		"ktm 125 duke", "/img/KTM%20125%20Duke.png",
		"ktm 125 rc", "/img/KTM%20125%20RC.png",
		"corny banane", "/img/Corny%20Banane.png"
	);

	private final ProduktRepository produktRepository;
	private final VerkaufRepository verkaufRepository;
	private final NachbestellungRepository nachbestellungRepository;

	public ProduktService(ProduktRepository produktRepository,
		VerkaufRepository verkaufRepository,
		NachbestellungRepository nachbestellungRepository) {
		this.produktRepository = produktRepository;
		this.verkaufRepository = verkaufRepository;
		this.nachbestellungRepository = nachbestellungRepository;
	}

	public List<Produkt> alleProdukte() {
		return produktRepository.findAllByOrderByNameAsc();
	}

	public List<String> alleKategorien() {
		return produktRepository.findeAlleKategorienSortiert();
	}

	public List<Produkt> produkteFilternUndSortieren(String name,
		String kategorie,
		Integer bestandVon,
		Integer bestandBis,
		BigDecimal preisVon,
		BigDecimal preisBis,
		String sortierung) {
		Comparator<Produkt> comparator = comparatorFuer(sortierung);
		return produktRepository.findAll().stream()
			.filter(p -> name == null || name.isBlank() || p.getName().toLowerCase(Locale.ROOT).contains(name.trim().toLowerCase(Locale.ROOT)))
			.filter(p -> kategorie == null || kategorie.isBlank() || p.getKategorie().equalsIgnoreCase(kategorie.trim()))
			.filter(p -> bestandVon == null || p.getBestand() >= bestandVon)
			.filter(p -> bestandBis == null || p.getBestand() <= bestandBis)
			.filter(p -> preisVon == null || p.getPreis().compareTo(preisVon) >= 0)
			.filter(p -> preisBis == null || p.getPreis().compareTo(preisBis) <= 0)
			.sorted(comparator)
			.toList();
	}

	public List<Produkt> produkteMitNiedrigemBestand() {
		return produktRepository.findAll().stream()
			.filter(Produkt::istNiedrigBestand)
			.sorted(Comparator.comparing(Produkt::getName, String.CASE_INSENSITIVE_ORDER))
			.toList();
	}

	public Produkt produktNachId(Long id) {
		return produktRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Produkt mit der ID " + id + " wurde nicht gefunden."));
	}

	public long anzahlProdukte() {
		return produktRepository.count();
	}

	public boolean produktIstLoeschbar(Long id) {
		return !verkaufRepository.existsByProduktId(id) && !nachbestellungRepository.existsByProduktId(id);
	}

	public Set<Long> nichtLoeschbareProduktIds(Collection<Produkt> produkte) {
		return produkte.stream()
			.map(Produkt::getId)
			.filter(id -> !produktIstLoeschbar(id))
			.collect(Collectors.toSet());
	}

	@Transactional
	public Produkt produktAnlegen(ProduktForm form) {
		pruefeEindeutigenNamen(form.getName(), null);
		Produkt produkt = new Produkt();
		aktualisiereAusForm(produkt, form);
		return produktRepository.save(produkt);
	}

	@Transactional
	public Produkt produktAktualisieren(Long id, ProduktForm form) {
		pruefeEindeutigenNamen(form.getName(), id);
		Produkt produkt = produktNachId(id);
		aktualisiereAusForm(produkt, form);
		return produktRepository.save(produkt);
	}

	@Transactional
	public Produkt bestandErhoehen(Long produktId, int menge) {
		Produkt produkt = produktNachId(produktId);
		produkt.bestandErhoehen(menge);
		return produktRepository.save(produkt);
	}

	@Transactional
	public Produkt bestandVerringern(Long produktId, int menge) {
		Produkt produkt = produktNachId(produktId);
		produkt.bestandVerringern(menge);
		return produktRepository.save(produkt);
	}

	@Transactional
	public void produktLoeschen(Long id) {
		Produkt produkt = produktNachId(id);
		if (verkaufRepository.existsByProduktId(id) || nachbestellungRepository.existsByProduktId(id)) {
			throw new IllegalArgumentException("Produkt kann nicht gelöscht werden, da bereits Verkäufe oder Nachbestellungen damit verknüpft sind.");
		}
		produktRepository.delete(produkt);
	}

	private void aktualisiereAusForm(Produkt produkt, ProduktForm form) {
		produkt.setName(form.getName().trim());
		produkt.setKategorie(form.getKategorie().trim());
		produkt.setBeschreibung(form.getBeschreibung() == null ? null : form.getBeschreibung().trim());
		String manuelleBildUrl = form.getBildUrl() == null || form.getBildUrl().isBlank() ? null : form.getBildUrl().trim();
		produkt.setBildUrl(manuelleBildUrl != null ? manuelleBildUrl : automatischeBildUrlFuer(form.getName()));
		produkt.setPreis(form.getPreis());
		produkt.setBestand(form.getBestand() == null ? 0 : form.getBestand());
		produkt.setMindestbestand(form.getMindestbestand() == null ? 0 : form.getMindestbestand());
	}

	private String automatischeBildUrlFuer(String produktName) {
		if (produktName == null || produktName.isBlank()) {
			return null;
		}
		String key = produktName.trim().toLowerCase(Locale.ROOT);
		return BILD_MAPPING.get(key);
	}

	private void pruefeEindeutigenNamen(String name, Long aktuelleProduktId) {
		produktRepository.findByNameIgnoreCase(name.trim())
			.filter(existing -> !existing.getId().equals(aktuelleProduktId))
			.ifPresent(existing -> {
				throw new IllegalArgumentException("Der Produktname ist bereits vergeben.");
			});
	}

	private Comparator<Produkt> comparatorFuer(String sortierung) {
		if ("bestand_asc".equals(sortierung)) {
			return Comparator.comparingInt(Produkt::getBestand);
		}
		if ("bestand_desc".equals(sortierung)) {
			return Comparator.comparingInt(Produkt::getBestand).reversed();
		}
		if ("preis_asc".equals(sortierung)) {
			return Comparator.comparing(Produkt::getPreis);
		}
		if ("preis_desc".equals(sortierung)) {
			return Comparator.comparing(Produkt::getPreis).reversed();
		}
		if ("kategorie".equals(sortierung)) {
			return Comparator.comparing(Produkt::getKategorie, String.CASE_INSENSITIVE_ORDER)
				.thenComparing(Produkt::getName, String.CASE_INSENSITIVE_ORDER);
		}
		return Comparator.comparing(Produkt::getName, String.CASE_INSENSITIVE_ORDER);
	}
}

