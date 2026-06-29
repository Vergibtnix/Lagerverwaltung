package com.lagerverwaltung.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.lagerverwaltung.backend.domain.Nachbestellung;
import com.lagerverwaltung.backend.domain.Produkt;
import com.lagerverwaltung.backend.domain.Verkauf;
import com.lagerverwaltung.backend.service.NachbestellungService;
import com.lagerverwaltung.backend.service.ProduktService;
import com.lagerverwaltung.backend.service.VerkaufService;
import com.lagerverwaltung.backend.web.form.NachbestellungForm;
import com.lagerverwaltung.backend.web.form.ProduktForm;
import com.lagerverwaltung.backend.web.form.VerkaufForm;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LagerverwaltungIntegrationTests {

	@Autowired
	private ProduktService produktService;

	@Autowired
	private VerkaufService verkaufService;

	@Autowired
	private NachbestellungService nachbestellungService;

	@Test
	void produktKannAngelegtWerdenUndErscheintInDerListe() {
		ProduktForm form = new ProduktForm();
		form.setName("Kartons");
		form.setKategorie("Logistik");
		form.setBeschreibung("Standard Versandkartons");
		form.setPreis(new BigDecimal("2.50"));
		form.setBestand(20);
		form.setMindestbestand(5);

		Produkt produkt = produktService.produktAnlegen(form);

		assertThat(produkt.getId()).isNotNull();
		assertThat(produktService.alleProdukte()).extracting(Produkt::getName).contains("Kartons");
	}

	@Test
	void verkaufReduziertDenBestandUndWirdGespeichert() {
		Produkt produkt = neuesProdukt("Etiketten", "A4 Etiketten", "1.20", 15, 4);

		VerkaufForm verkaufForm = new VerkaufForm();
		verkaufForm.setProduktId(produkt.getId());
		verkaufForm.setMenge(3);

		Verkauf verkauf = verkaufService.verkaufErfassen(verkaufForm);

		assertThat(verkauf.getId()).isNotNull();
		assertThat(verkauf.getGesamtpreis()).isEqualByComparingTo("3.60");
		assertThat(produktService.produktNachId(produkt.getId()).getBestand()).isEqualTo(12);
		assertThat(verkaufService.alleVerkaeufe()).hasSize(1);
	}

	@Test
	void verkaufLoeschenKorrigiertDenBestand() {
		Produkt produkt = neuesProdukt("Marker", "Permanentmarker", "2.00", 5, 1);

		VerkaufForm verkaufForm = new VerkaufForm();
		verkaufForm.setProduktId(produkt.getId());
		verkaufForm.setMenge(7);

		Verkauf verkauf = verkaufService.verkaufErfassen(verkaufForm);
		assertThat(produktService.produktNachId(produkt.getId()).getBestand()).isEqualTo(-2);

		verkaufService.verkaufLoeschen(verkauf.getId());
		assertThat(produktService.produktNachId(produkt.getId()).getBestand()).isEqualTo(5);
	}

	@Test
	void nachbestellungKannErfasstUndAlsEingetroffenMarkiertWerden() {
		Produkt produkt = neuesProdukt("Paletten", "Holzpaletten", "9.99", 2, 10);

		NachbestellungForm form = new NachbestellungForm();
		form.setProduktId(produkt.getId());
		form.setMenge(8);
		form.setLieferant("Musterlieferant");
		form.setBemerkung("Dringend nachbestellen");

		Nachbestellung nachbestellung = nachbestellungService.nachbestellungErfassen(form);

		assertThat(nachbestellung.getId()).isNotNull();
		assertThat(nachbestellungService.offeneNachbestellungen()).hasSize(1);

		nachbestellungService.nachbestellungAlsEingetroffenMarkieren(nachbestellung.getId());

		assertThat(produktService.produktNachId(produkt.getId()).getBestand()).isEqualTo(10);
		assertThat(nachbestellungService.offeneNachbestellungen()).isEmpty();
	}

	@Test
	void unverknuepftesProduktKannGeloeschtWerden() {
		Produkt produkt = neuesProdukt("Klebeband", "Transparent", "1.99", 12, 2);

		produktService.produktLoeschen(produkt.getId());

		assertThat(produktService.alleProdukte()).extracting(Produkt::getName).doesNotContain("Klebeband");
	}

	private Produkt neuesProdukt(String name, String beschreibung, String preis, int bestand, int mindestbestand) {
		ProduktForm form = new ProduktForm();
		form.setName(name);
		form.setKategorie("Testkategorie");
		form.setBeschreibung(beschreibung);
		form.setPreis(new BigDecimal(preis));
		form.setBestand(bestand);
		form.setMindestbestand(mindestbestand);
		return produktService.produktAnlegen(form);
	}
}

