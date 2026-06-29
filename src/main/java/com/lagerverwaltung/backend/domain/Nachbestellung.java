package com.lagerverwaltung.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nachbestellungen")
public class Nachbestellung {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produkt_id", nullable = false)
	private Produkt produkt;

	@Column(nullable = false)
	private int menge;

	@Column(length = 255)
	private String lieferant;

	@Column(length = 1000)
	private String bemerkung;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private NachbestellStatus status = NachbestellStatus.OFFEN;

	@Column(nullable = false, updatable = false)
	private LocalDateTime bestelltAm;

	private LocalDate voraussichtlichesLieferdatum;

	private LocalDate tatsaechlichesLieferdatum;

	public Nachbestellung() {
	}

	@PrePersist
	void vorSpeichern() {
		if (bestelltAm == null) {
			bestelltAm = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Produkt getProdukt() {
		return produkt;
	}

	public void setProdukt(Produkt produkt) {
		this.produkt = produkt;
	}

	public int getMenge() {
		return menge;
	}

	public void setMenge(int menge) {
		this.menge = menge;
	}

	public String getLieferant() {
		return lieferant;
	}

	public void setLieferant(String lieferant) {
		this.lieferant = lieferant;
	}

	public String getBemerkung() {
		return bemerkung;
	}

	public void setBemerkung(String bemerkung) {
		this.bemerkung = bemerkung;
	}

	public NachbestellStatus getStatus() {
		return status;
	}

	public void setStatus(NachbestellStatus status) {
		this.status = status;
	}

	public LocalDateTime getBestelltAm() {
		return bestelltAm;
	}

	public LocalDate getVoraussichtlichesLieferdatum() {
		return voraussichtlichesLieferdatum;
	}

	public void setVoraussichtlichesLieferdatum(LocalDate voraussichtlichesLieferdatum) {
		this.voraussichtlichesLieferdatum = voraussichtlichesLieferdatum;
	}

	public LocalDate getTatsaechlichesLieferdatum() {
		return tatsaechlichesLieferdatum;
	}

	public void alsEingetroffenMarkieren() {
		this.status = NachbestellStatus.EINGETROFFEN;
		this.tatsaechlichesLieferdatum = LocalDate.now();
	}
}

