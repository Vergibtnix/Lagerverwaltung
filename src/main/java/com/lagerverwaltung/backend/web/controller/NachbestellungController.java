package com.lagerverwaltung.backend.web.controller;

import com.lagerverwaltung.backend.service.NachbestellungService;
import com.lagerverwaltung.backend.service.ProduktService;
import com.lagerverwaltung.backend.web.form.NachbestellungForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nachbestellungen")
public class NachbestellungController {

	private final NachbestellungService nachbestellungService;
	private final ProduktService produktService;

	public NachbestellungController(NachbestellungService nachbestellungService, ProduktService produktService) {
		this.nachbestellungService = nachbestellungService;
		this.produktService = produktService;
	}

	@GetMapping
	public String liste(Model model) {
		model.addAttribute("nachbestellungen", nachbestellungService.alleNachbestellungen());
		model.addAttribute("offeneNachbestellungen", nachbestellungService.offeneNachbestellungen());
		return "nachbestellungen/list";
	}

	@GetMapping("/neu")
	public String neuesFormular(@RequestParam(required = false) Long produktId, Model model) {
		if (!model.containsAttribute("nachbestellungForm")) {
			NachbestellungForm form = new NachbestellungForm();
			if (produktId != null) {
				produktService.produktNachId(produktId);
				form.setProduktId(produktId);
			}
			model.addAttribute("nachbestellungForm", form);
		}
		model.addAttribute("produkte", produktService.alleProdukte());
		return "nachbestellungen/form";
	}

	@PostMapping
	public String anlegen(@Validated @ModelAttribute("nachbestellungForm") NachbestellungForm nachbestellungForm,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("produkte", produktService.alleProdukte());
			return "nachbestellungen/form";
		}

		try {
			nachbestellungService.nachbestellungErfassen(nachbestellungForm);
			redirectAttributes.addFlashAttribute("successMessage", "Nachbestellung wurde erfolgreich erfasst.");
			return "redirect:/nachbestellungen";
		} catch (IllegalArgumentException ex) {
			bindingResult.reject("nachbestellung.fehler", ex.getMessage());
			model.addAttribute("produkte", produktService.alleProdukte());
			return "nachbestellungen/form";
		}
	}

	@PostMapping("/{id}/eingetroffen")
	public String alsEingetroffenMarkieren(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			nachbestellungService.nachbestellungAlsEingetroffenMarkieren(id);
			redirectAttributes.addFlashAttribute("successMessage", "Nachbestellung wurde als eingetroffen markiert.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/nachbestellungen";
	}

	@PostMapping("/{id}/loeschen")
	public String loeschen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			nachbestellungService.nachbestellungLoeschen(id);
			redirectAttributes.addFlashAttribute("successMessage", "Nachbestellung wurde gelöscht.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/nachbestellungen";
	}
}

