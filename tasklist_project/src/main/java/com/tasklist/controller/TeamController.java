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
        model.addAttribute("teams", teamRepository.findAll());
        return "teams";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("team", new Team());
        return "add-team";
    }

    @PostMapping("/add")
    public String save(@Valid @ModelAttribute Team team, BindingResult br) {
        if (br.hasErrors()) return "add-team";
        teamRepository.save(team);
        return "redirect:/teams";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Team> t = teamRepository.findById(id);
        if (t.isPresent()) {
            model.addAttribute("team", t.get());
            return "edit-team";
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

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        teamRepository.deleteById(id);
        return "redirect:/teams";
    }
}

