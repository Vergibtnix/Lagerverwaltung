package com.lagerverwaltung.backend.web.controller;

import com.lagerverwaltung.backend.service.NachbestellungService;
import com.lagerverwaltung.backend.service.ProduktService;
import com.lagerverwaltung.backend.service.VerkaufService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UebersichtController {

	private final ProduktService produktService;
	private final VerkaufService verkaufService;
	private final NachbestellungService nachbestellungService;

	public UebersichtController(ProduktService produktService, VerkaufService verkaufService, NachbestellungService nachbestellungService) {
		this.produktService = produktService;
		this.verkaufService = verkaufService;
		this.nachbestellungService = nachbestellungService;
	}

	@GetMapping({"/", "/uebersicht"})
	public String index(Model model) {
		model.addAttribute("anzahlProdukte", produktService.anzahlProdukte());
		model.addAttribute("produkteMitNiedrigemBestand", produktService.produkteMitNiedrigemBestand());
		model.addAttribute("letzteVerkaeufe", verkaufService.letzteVerkaeufe(5));
		model.addAttribute("offeneNachbestellungen", nachbestellungService.offeneNachbestellungen());
		model.addAttribute("umsatzGesamt", verkaufService.umsatzGesamt());
		return "uebersicht/index";
	}
}

