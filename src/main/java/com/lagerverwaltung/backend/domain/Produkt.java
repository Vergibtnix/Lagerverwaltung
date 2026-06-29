package com.lagerverwaltung.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produkte")
public class Produkt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100, unique = true)
	private String name;

	@Column(nullable = false, length = 100)
	private String kategorie;

	@Column(length = 5000)
	private String beschreibung;

	@Column(name = "bild_url", length = 500)
	private String bildUrl;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal preis = BigDecimal.ZERO;

	@Column(nullable = false)
	private int bestand = 0;

	@Column(nullable = false)
	private int mindestbestand = 0;

	@Column(nullable = false, updatable = false)
	private LocalDateTime erstelltAm;

	@Column(nullable = false)
	private LocalDateTime aktualisiertAm;

	public Produkt() {
	}

	public Produkt(String name, String kategorie, String beschreibung, String bildUrl, BigDecimal preis, int bestand, int mindestbestand) {
		this.name = name;
		this.kategorie = kategorie;
		this.beschreibung = beschreibung;
		this.bildUrl = bildUrl;
		this.preis = preis;
		this.bestand = bestand;
		this.mindestbestand = mindestbestand;
	}

	@PrePersist
	void vorSpeichern() {
		LocalDateTime jetzt = LocalDateTime.now();
		erstelltAm = jetzt;
		aktualisiertAm = jetzt;
	}

	@PreUpdate
	void vorAktualisierung() {
		aktualisiertAm = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKategorie() {
		return kategorie;
	}

	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBildUrl() {
		return bildUrl;
	}

	public void setBildUrl(String bildUrl) {
		this.bildUrl = bildUrl;
	}

	public BigDecimal getPreis() {
		return preis;
	}

	public void setPreis(BigDecimal preis) {
		this.preis = preis;
	}

	public int getBestand() {
		return bestand;
	}

	public void setBestand(int bestand) {
		this.bestand = bestand;
	}

	public int getMindestbestand() {
		return mindestbestand;
	}

	public void setMindestbestand(int mindestbestand) {
		this.mindestbestand = mindestbestand;
	}

	public LocalDateTime getErstelltAm() {
		return erstelltAm;
	}

	public LocalDateTime getAktualisiertAm() {
		return aktualisiertAm;
	}

	public boolean istNiedrigBestand() {
		return bestand <= mindestbestand;
	}

	public boolean istNachzuliefern() {
		return bestand < 0;
	}

	public void bestandErhoehen(int menge) {
		if (menge < 0) {
			throw new IllegalArgumentException("Die Menge zum Erhöhen des Bestands darf nicht negativ sein.");
		}
		bestand += menge;
	}

	public void bestandVerringern(int menge) {
		if (menge < 0) {
			throw new IllegalArgumentException("Die Menge zum Verringern des Bestands darf nicht negativ sein.");
		}
		bestand -= menge;
	}
}

