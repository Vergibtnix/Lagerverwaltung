package com.lagerverwaltung.backend.web.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class NachbestellungForm {

	@NotNull(message = "Bitte ein Produkt auswählen.")
	private Long produktId;

	@NotNull(message = "Die Nachbestellmenge ist erforderlich.")
	@Min(value = 1, message = "Die Nachbestellmenge muss mindestens 1 betragen.")
	private Integer menge = 1;

	@Size(max = 255, message = "Der Lieferant darf höchstens 255 Zeichen haben.")
	private String lieferant;

	@Size(max = 1000, message = "Die Bemerkung darf höchstens 1000 Zeichen haben.")
	private String bemerkung;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate voraussichtlichesLieferdatum;

	public Long getProduktId() {
		return produktId;
	}

	public void setProduktId(Long produktId) {
		this.produktId = produktId;
	}

	public Integer getMenge() {
		return menge;
	}

	public void setMenge(Integer menge) {
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

	public LocalDate getVoraussichtlichesLieferdatum() {
		return voraussichtlichesLieferdatum;
	}

	public void setVoraussichtlichesLieferdatum(LocalDate voraussichtlichesLieferdatum) {
		this.voraussichtlichesLieferdatum = voraussichtlichesLieferdatum;
	}
}

