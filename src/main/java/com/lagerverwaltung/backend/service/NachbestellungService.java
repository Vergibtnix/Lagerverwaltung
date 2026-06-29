package com.lagerverwaltung.backend.service;

import com.lagerverwaltung.backend.domain.NachbestellStatus;
import com.lagerverwaltung.backend.domain.Nachbestellung;
import com.lagerverwaltung.backend.domain.Produkt;
import com.lagerverwaltung.backend.repository.NachbestellungRepository;
import com.lagerverwaltung.backend.repository.ProduktRepository;
import com.lagerverwaltung.backend.web.form.NachbestellungForm;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NachbestellungService {

	private final NachbestellungRepository nachbestellungRepository;
	private final ProduktRepository produktRepository;

	public NachbestellungService(NachbestellungRepository nachbestellungRepository, ProduktRepository produktRepository) {
		this.nachbestellungRepository = nachbestellungRepository;
		this.produktRepository = produktRepository;
	}

	public List<Nachbestellung> alleNachbestellungen() {
		return nachbestellungRepository.findAllByOrderByBestelltAmDesc();
	}

	public List<Nachbestellung> offeneNachbestellungen() {
		return nachbestellungRepository.findByStatusOrderByBestelltAmDesc(NachbestellStatus.OFFEN);
	}

	@Transactional
	public Nachbestellung nachbestellungErfassen(NachbestellungForm form) {
		if (form.getMenge() == null || form.getMenge() <= 0) {
			throw new IllegalArgumentException("Die Nachbestellmenge muss größer als 0 sein.");
		}

		Produkt produkt = produktRepository.findById(form.getProduktId())
			.orElseThrow(() -> new IllegalArgumentException("Das ausgewählte Produkt wurde nicht gefunden."));

		Nachbestellung nachbestellung = new Nachbestellung();
		nachbestellung.setProdukt(produkt);
		nachbestellung.setMenge(form.getMenge());
		nachbestellung.setLieferant(form.getLieferant() == null ? null : form.getLieferant().trim());
		nachbestellung.setBemerkung(form.getBemerkung() == null ? null : form.getBemerkung().trim());
		nachbestellung.setVoraussichtlichesLieferdatum(form.getVoraussichtlichesLieferdatum());
		nachbestellung.setStatus(NachbestellStatus.OFFEN);

		return nachbestellungRepository.save(nachbestellung);
	}

	@Transactional
	public Nachbestellung nachbestellungAlsEingetroffenMarkieren(Long id) {
		Nachbestellung nachbestellung = nachbestellungRepository.findOneById(id)
			.orElseThrow(() -> new IllegalArgumentException("Die Nachbestellung wurde nicht gefunden."));

		if (nachbestellung.getStatus() != NachbestellStatus.EINGETROFFEN) {
			Produkt produkt = nachbestellung.getProdukt();
			produkt.bestandErhoehen(nachbestellung.getMenge());
			produktRepository.save(produkt);
			nachbestellung.alsEingetroffenMarkieren();
			nachbestellungRepository.save(nachbestellung);
		}

		return nachbestellung;
	}

	@Transactional
	public void nachbestellungLoeschen(Long id) {
		Nachbestellung nachbestellung = nachbestellungRepository.findOneById(id)
			.orElseThrow(() -> new IllegalArgumentException("Die Nachbestellung wurde nicht gefunden."));
		if (nachbestellung.getStatus() == NachbestellStatus.EINGETROFFEN) {
			throw new IllegalArgumentException("Eingegangene Nachbestellungen können nicht mehr gelöscht werden.");
		}
		nachbestellungRepository.delete(nachbestellung);
	}
}

