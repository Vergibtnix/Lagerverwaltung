package com.lagerverwaltung.backend.service;

import com.lagerverwaltung.backend.domain.Produkt;
import com.lagerverwaltung.backend.domain.Verkauf;
import com.lagerverwaltung.backend.repository.ProduktRepository;
import com.lagerverwaltung.backend.repository.VerkaufRepository;
import com.lagerverwaltung.backend.web.form.VerkaufForm;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VerkaufService {

	private final VerkaufRepository verkaufRepository;
	private final ProduktRepository produktRepository;

	public VerkaufService(VerkaufRepository verkaufRepository, ProduktRepository produktRepository) {
		this.verkaufRepository = verkaufRepository;
		this.produktRepository = produktRepository;
	}

	public List<Verkauf> alleVerkaeufe() {
		return verkaufRepository.findAllByOrderByVerkauftAmDesc();
	}

	public BigDecimal umsatzGesamt() {
		return verkaufRepository.berechneUmsatzGesamt();
	}

	public List<Verkauf> letzteVerkaeufe(int anzahl) {
		return verkaufRepository.findAllByOrderByVerkauftAmDesc().stream().limit(Math.max(anzahl, 0)).toList();
	}

	@Transactional
	public Verkauf verkaufErfassen(VerkaufForm form) {
		if (form.getMenge() == null || form.getMenge() <= 0) {
			throw new IllegalArgumentException("Die Verkaufsmenge muss größer als 0 sein.");
		}

		Produkt produkt = produktRepository.findById(form.getProduktId())
			.orElseThrow(() -> new IllegalArgumentException("Das ausgewählte Produkt wurde nicht gefunden."));

		produkt.bestandVerringern(form.getMenge());
		produktRepository.save(produkt);

		BigDecimal rabattFaktor = BigDecimal.valueOf(100 - form.getRabattProzent()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
		BigDecimal einzelpreis = produkt.getPreis().multiply(rabattFaktor).setScale(2, RoundingMode.HALF_UP);
		LocalDateTime verkauftAm = form.getVerkauftAm() != null ? form.getVerkauftAm() : LocalDateTime.now();

		Verkauf verkauf = new Verkauf();
		verkauf.setProdukt(produkt);
		verkauf.setMenge(form.getMenge());
		verkauf.setKundenname(form.getKundenname() == null ? null : form.getKundenname().trim());
		verkauf.setEinzelpreis(einzelpreis);
		verkauf.setGesamtpreis(einzelpreis.multiply(BigDecimal.valueOf(form.getMenge())));
		verkauf.setVerkauftAm(verkauftAm);

		return verkaufRepository.save(verkauf);
	}

	@Transactional
	public void verkaufLoeschen(Long id) {
		Verkauf verkauf = verkaufRepository.findOneById(id)
			.orElseThrow(() -> new IllegalArgumentException("Der Verkauf wurde nicht gefunden."));
		Produkt produkt = verkauf.getProdukt();
		produkt.bestandErhoehen(verkauf.getMenge());
		produktRepository.save(produkt);
		verkaufRepository.delete(verkauf);
	}
}

