package com.lagerverwaltung.backend.web.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ProduktForm {

	@NotBlank(message = "Der Produktname ist erforderlich.")
	@Size(max = 100, message = "Der Produktname darf höchstens 100 Zeichen haben.")
	private String name;

	@NotBlank(message = "Die Produktkategorie ist erforderlich.")
	@Size(max = 100, message = "Die Kategorie darf höchstens 100 Zeichen haben.")
	private String kategorie;

	@Size(max = 5000, message = "Die Beschreibung darf höchstens 5000 Zeichen haben.")
	private String beschreibung;

	@Size(max = 500, message = "Die Bild-URL darf höchstens 500 Zeichen haben.")
	@Pattern(regexp = "^(https?://.*|/.*)?$", message = "Bitte eine gültige URL (http/https) oder einen absoluten Pfad (/...) angeben.")
	private String bildUrl;

	@NotNull(message = "Der Preis ist erforderlich.")
	@DecimalMin(value = "0.00", inclusive = true, message = "Der Preis darf nicht negativ sein.")
	private BigDecimal preis = BigDecimal.ZERO;

	@NotNull(message = "Der Bestand ist erforderlich.")
	@Min(value = 0, message = "Der Bestand darf nicht negativ sein.")
	@Max(value = Integer.MAX_VALUE, message = "Der Bestand ist zu groß.")
	private Integer bestand = 0;

	@NotNull(message = "Der Mindestbestand ist erforderlich.")
	@Min(value = 0, message = "Der Mindestbestand darf nicht negativ sein.")
	private Integer mindestbestand = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String getKategorie() {
		return kategorie;
	}

	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
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

	public Integer getBestand() {
		return bestand;
	}

	public void setBestand(Integer bestand) {
		this.bestand = bestand;
	}

	public Integer getMindestbestand() {
		return mindestbestand;
	}

	public void setMindestbestand(Integer mindestbestand) {
		this.mindestbestand = mindestbestand;
	}
}

