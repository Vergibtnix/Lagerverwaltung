package com.lagerverwaltung.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "verkaeufe")
public class Verkauf {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produkt_id", nullable = false)
	private Produkt produkt;

	@Column(nullable = false)
	private int menge;

	@Column(length = 255)
	private String kundenname;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal einzelpreis = BigDecimal.ZERO;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal gesamtpreis = BigDecimal.ZERO;

	@Column(nullable = false, updatable = false)
	private LocalDateTime verkauftAm;

	public Verkauf() {
	}

	@PrePersist
	void vorSpeichern() {
		if (verkauftAm == null) {
			verkauftAm = LocalDateTime.now();
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

	public String getKundenname() {
		return kundenname;
	}

	public void setKundenname(String kundenname) {
		this.kundenname = kundenname;
	}

	public BigDecimal getEinzelpreis() {
		return einzelpreis;
	}

	public void setEinzelpreis(BigDecimal einzelpreis) {
		this.einzelpreis = einzelpreis;
	}

	public BigDecimal getGesamtpreis() {
		return gesamtpreis;
	}

	public void setGesamtpreis(BigDecimal gesamtpreis) {
		this.gesamtpreis = gesamtpreis;
	}

	public LocalDateTime getVerkauftAm() {
		return verkauftAm;
	}

	public void setVerkauftAm(LocalDateTime verkauftAm) {
		this.verkauftAm = verkauftAm;
	}
}

