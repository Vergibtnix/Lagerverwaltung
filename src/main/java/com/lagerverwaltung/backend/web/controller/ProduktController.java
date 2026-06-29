package com.lagerverwaltung.backend.web.controller;

import com.lagerverwaltung.backend.service.ProduktService;
import com.lagerverwaltung.backend.domain.Produkt;
import com.lagerverwaltung.backend.web.form.ProduktForm;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produkte")
public class ProduktController {

	private final ProduktService produktService;

	public ProduktController(ProduktService produktService) {
		this.produktService = produktService;
	}

	@GetMapping
	public String liste(@RequestParam(required = false) String name,
		@RequestParam(required = false) String kategorie,
		@RequestParam(required = false) Integer bestandVon,
		@RequestParam(required = false) Integer bestandBis,
		@RequestParam(required = false) BigDecimal preisVon,
		@RequestParam(required = false) BigDecimal preisBis,
		@RequestParam(defaultValue = "name") String sortierung,
		Model model) {
		var produkte = produktService.produkteFilternUndSortieren(name, kategorie, bestandVon, bestandBis, preisVon, preisBis, sortierung);
		model.addAttribute("produkte", produkte);
		model.addAttribute("nichtLoeschbareProduktIds", produktService.nichtLoeschbareProduktIds(produkte));
		model.addAttribute("niedrigBestandProdukte", produktService.produkteMitNiedrigemBestand());
		model.addAttribute("kategorien", produktService.alleKategorien());
		model.addAttribute("name", name);
		model.addAttribute("kategorie", kategorie);
		model.addAttribute("bestandVon", bestandVon);
		model.addAttribute("bestandBis", bestandBis);
		model.addAttribute("preisVon", preisVon);
		model.addAttribute("preisBis", preisBis);
		model.addAttribute("sortierung", sortierung);
		return "produkte/list";
	}

	@GetMapping("/neu")
	public String neuesProdukt(Model model) {
		if (!model.containsAttribute("produktForm")) {
			model.addAttribute("produktForm", new ProduktForm());
		}
		model.addAttribute("kategorien", produktService.alleKategorien());
		model.addAttribute("isEdit", false);
		return "produkte/form";
	}

	@GetMapping("/{id}/bearbeiten")
	public String bearbeiten(@PathVariable Long id, Model model) {
		Produkt produkt = produktService.produktNachId(id);
		if (!model.containsAttribute("produktForm")) {
			ProduktForm form = new ProduktForm();
			form.setName(produkt.getName());
			form.setKategorie(produkt.getKategorie());
			form.setBeschreibung(produkt.getBeschreibung());
			form.setBildUrl(produkt.getBildUrl());
			form.setPreis(produkt.getPreis());
			form.setBestand(produkt.getBestand());
			form.setMindestbestand(produkt.getMindestbestand());
			model.addAttribute("produktForm", form);
		}
		model.addAttribute("produktId", id);
		model.addAttribute("kategorien", produktService.alleKategorien());
		model.addAttribute("isEdit", true);
		return "produkte/form";
	}

	@PostMapping
	public String anlegen(@Valid @ModelAttribute("produktForm") ProduktForm produktForm,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("kategorien", produktService.alleKategorien());
			model.addAttribute("isEdit", false);
			return "produkte/form";
		}

		try {
			produktService.produktAnlegen(produktForm);
			redirectAttributes.addFlashAttribute("successMessage", "Produkt wurde erfolgreich angelegt.");
			return "redirect:/produkte";
		} catch (IllegalArgumentException ex) {
			bindingResult.reject("produkt.fehler", ex.getMessage());
			model.addAttribute("kategorien", produktService.alleKategorien());
			model.addAttribute("isEdit", false);
			return "produkte/form";
		}
	}

	@PostMapping("/{id}")
	public String aktualisieren(@PathVariable Long id,
		@Valid @ModelAttribute("produktForm") ProduktForm produktForm,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("produktId", id);
			model.addAttribute("kategorien", produktService.alleKategorien());
			model.addAttribute("isEdit", true);
			return "produkte/form";
		}

		try {
			produktService.produktAktualisieren(id, produktForm);
			redirectAttributes.addFlashAttribute("successMessage", "Produkt wurde erfolgreich aktualisiert.");
			return "redirect:/produkte";
		} catch (IllegalArgumentException ex) {
			bindingResult.reject("produkt.fehler", ex.getMessage());
			model.addAttribute("produktId", id);
			model.addAttribute("kategorien", produktService.alleKategorien());
			model.addAttribute("isEdit", true);
			return "produkte/form";
		}
	}

	@PostMapping("/{id}/loeschen")
	public String loeschen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			produktService.produktLoeschen(id);
			redirectAttributes.addFlashAttribute("successMessage", "Produkt wurde erfolgreich gelöscht.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/produkte";
	}
}

