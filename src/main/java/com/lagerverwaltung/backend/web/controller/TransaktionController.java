package com.lagerverwaltung.backend.web.controller;

import com.lagerverwaltung.backend.service.NachbestellungService;
import com.lagerverwaltung.backend.service.VerkaufService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transaktionen")
public class TransaktionController {

	private final VerkaufService verkaufService;
	private final NachbestellungService nachbestellungService;

	public TransaktionController(VerkaufService verkaufService, NachbestellungService nachbestellungService) {
		this.verkaufService = verkaufService;
		this.nachbestellungService = nachbestellungService;
	}

	@GetMapping
	public String liste(Model model) {
		model.addAttribute("verkaeufe", verkaufService.alleVerkaeufe());
		model.addAttribute("nachbestellungen", nachbestellungService.alleNachbestellungen());
		return "transaktionen/list";
	}
}

