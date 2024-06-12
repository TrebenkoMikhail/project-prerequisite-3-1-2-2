package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RegisterController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute User user, Model model) {
        model.addAttribute("user", user);
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        List<Role> allRoles= roleRepository.findAll();
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/login";
    }

}
