package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RoleRepository roleRepository;


    @GetMapping(value ="/admin")
    private String adminHome(Model model){
        model.addAttribute("user", userService.getAllUsers());
        return "admin";
    }

    @GetMapping(value = "/admin/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }
    @PostMapping(value = "/admin/add")
    public String addUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/admin";
    }
    @GetMapping("/admin/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "user-edit";
    }
    @PostMapping("/admin/edit")
    public String editUserSubmit(@ModelAttribute User user) {
        userService.updateUser(user);
        return "redirect:/admin";
    }
    @GetMapping(value = "/admin/delete/{id}")
    public String deleteUserById(@PathVariable("id") Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUserById(id);
        }
        return "redirect:/admin";
    }
}
