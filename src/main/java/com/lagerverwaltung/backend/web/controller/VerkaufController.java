package com.lagerverwaltung.backend.web.controller;

import com.lagerverwaltung.backend.service.ProduktService;
import com.lagerverwaltung.backend.service.VerkaufService;
import com.lagerverwaltung.backend.web.form.VerkaufForm;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/verkaeufe")
public class VerkaufController {

	private final VerkaufService verkaufService;
	private final ProduktService produktService;

	public VerkaufController(VerkaufService verkaufService, ProduktService produktService) {
		this.verkaufService = verkaufService;
		this.produktService = produktService;
	}

	@GetMapping
	public String liste(Model model) {
		model.addAttribute("verkaeufe", verkaufService.alleVerkaeufe());
		model.addAttribute("umsatzGesamt", verkaufService.umsatzGesamt());
		return "verkaeufe/list";
	}

	@GetMapping("/neu")
	public String neuesFormular(Model model) {
		if (!model.containsAttribute("verkaufForm")) {
			model.addAttribute("verkaufForm", new VerkaufForm());
		}
		model.addAttribute("produkte", produktService.alleProdukte());
		model.addAttribute("rabattOptionen", List.of(0, 5, 10, 15, 20, 25));
		return "verkaeufe/form";
	}

	@PostMapping
	public String anlegen(@Valid @ModelAttribute("verkaufForm") VerkaufForm verkaufForm,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("produkte", produktService.alleProdukte());
			model.addAttribute("rabattOptionen", List.of(0, 5, 10, 15, 20, 25));
			return "verkaeufe/form";
		}

		try {
			verkaufService.verkaufErfassen(verkaufForm);
			redirectAttributes.addFlashAttribute("successMessage", "Verkauf wurde erfolgreich erfasst.");
			return "redirect:/verkaeufe";
		} catch (IllegalArgumentException ex) {
			bindingResult.reject("verkauf.fehler", ex.getMessage());
			model.addAttribute("produkte", produktService.alleProdukte());
			model.addAttribute("rabattOptionen", List.of(0, 5, 10, 15, 20, 25));
			return "verkaeufe/form";
		}
	}

	@PostMapping("/{id}/loeschen")
	public String loeschen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			verkaufService.verkaufLoeschen(id);
			redirectAttributes.addFlashAttribute("successMessage", "Verkauf wurde gelöscht und Bestand korrigiert.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/verkaeufe";
	}
}

