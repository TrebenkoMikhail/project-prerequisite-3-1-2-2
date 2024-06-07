package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isUserAdmin(authentication)) {
            return "redirect:/access-denied";
        }

        User user = userService.getUserById(id);
        model.addAttribute("user",user);
        return "edit-user";
    }

    @PostMapping("/admin/edit")
    public String editUserSubmit(@ModelAttribute User user, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isUserAdmin(authentication)) {
            return "redirect:/access-denied";
        }
        String errorMessage = validateUser(user);
        if(errorMessage != null) {
            model.addAttribute("error", errorMessage);
            return "edit-user";
        }
        System.out.println("Updating user with id: " + user.getId());
        userService.updateUser(user);
        return "redirect:/admin";
    }
    @GetMapping(value = "/admin/delete/{id}")
    public String deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }
    private boolean isUserAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private String validateUser(User user) {
        if(user.getUsername() == null || user.getUsername().isEmpty()) {
            return "Username cannot be empty";
        }
        if(user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Password cannot be empty";
        }
        if(user.getEmail() == null || user.getEmail().isEmpty()) {
            return "Email cannot be empty";
        }
        if(user.getFirstname() == null || user.getFirstname().isEmpty()) {
            return "Firstname cannot be empty";
        }
        if(user.getLastname() == null || user.getLastname().isEmpty()) {
            return "Lastname cannot be empty";
        }
        return null;
    }
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
    @GetMapping("/error")
    public String handleError() {
        return "error";
    }
}
