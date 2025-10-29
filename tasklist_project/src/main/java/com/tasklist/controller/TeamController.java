// src/main/java/com/tasklist/controller/TeamController.java
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

   @GetMapping
    public String list(Model model) {
        // Obtenemos solo los equipos activos
        model.addAttribute("teams", teamRepository.findByActiveTrue());
        model.addAttribute("newTeam", new Team()); 
        return "teams";
    }

    @PostMapping("/add")
    public String save(@Valid @ModelAttribute("newTeam") Team team, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "redirect:/teams";
        }
        teamRepository.save(team);
        return "redirect:/teams";
    }

    // ===== Formulario para editar equipo (CARGA AJAX) =====
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Team> t = teamRepository.findById(id);
        if (t.isPresent()) {
            model.addAttribute("team", t.get());
            // Retorna solo el fragmento del formulario.
            return "edit-team :: teamEditForm"; 
        }
        return "redirect:/teams";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute Team team, BindingResult br) {
        if (br.hasErrors()) return "edit-team";
        team.setId(id);
        teamRepository.save(team);
        return "redirect:/teams";
    }

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