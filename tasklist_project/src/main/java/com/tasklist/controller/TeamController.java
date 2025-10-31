package com.tasklist.controller;

import com.tasklist.model.Team;
import com.tasklist.repository.TeamRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    // ===== Lista de equipos activos =====
    @GetMapping
    public String list(Model model) {
        model.addAttribute("teams", teamRepository.findByActiveTrue());
        model.addAttribute("newTeam", new Team());
        return "teams";
    }

    // ===== Crear nuevo equipo =====
    @PostMapping("/add")
    public String save(@Valid @ModelAttribute("newTeam") Team team, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("teams", teamRepository.findByActiveTrue());
            return "teams";
        }
        teamRepository.save(team);
        return "redirect:/teams";
    }

    // ===== Formulario para editar equipo (modal) =====
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Team> t = teamRepository.findById(id);
        if (t.isPresent()) {
            model.addAttribute("team", t.get());
            return "edit-team :: teamEditForm"; // Fragmento Thymeleaf
        }
        return "redirect:/teams";
    }

    // ===== Guardar cambios del equipo =====
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute Team team, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("team", team);
            return "edit-team :: teamEditForm";
        }
        team.setId(id);
        teamRepository.save(team);
        return "redirect:/teams";
    }

    // ===== Eliminación lógica del equipo =====
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Optional<Team> t = teamRepository.findById(id);
        if (t.isPresent()) {
            Team team = t.get();
            team.setActive(false);
            teamRepository.save(team);
        }
        return "redirect:/teams";
    }
}
