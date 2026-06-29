package com.lagerverwaltung.backend.web.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class VerkaufForm {

	@NotNull(message = "Bitte ein Produkt auswählen.")
	private Long produktId;

	@NotNull(message = "Die Verkaufsmenge ist erforderlich.")
	@Min(value = 1, message = "Die Verkaufsmenge muss mindestens 1 betragen.")
	private Integer menge = 1;

	@Size(max = 255, message = "Der Kundenname darf höchstens 255 Zeichen haben.")
	private String kundenname;

	@NotNull(message = "Bitte einen Rabatt oder Skonto auswählen.")
	@Min(value = 0, message = "Der Rabatt darf nicht negativ sein.")
	@Max(value = 100, message = "Der Rabatt darf nicht über 100 % liegen.")
	private Integer rabattProzent = 0;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime verkauftAm;

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

	public String getKundenname() {
		return kundenname;
	}

	public void setKundenname(String kundenname) {
		this.kundenname = kundenname;
	}

	public Integer getRabattProzent() {
		return rabattProzent;
	}

	public void setRabattProzent(Integer rabattProzent) {
		this.rabattProzent = rabattProzent;
	}

	public LocalDateTime getVerkauftAm() {
		return verkauftAm;
	}

	public void setVerkauftAm(LocalDateTime verkauftAm) {
		this.verkauftAm = verkauftAm;
	}
}

