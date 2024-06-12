package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.repository.UserRepository;
import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping(value ="/admin")
    private String adminHome(Model model){
        model.addAttribute("user", userService.getAllUsers());
        return "admin";
    }

    @GetMapping(value = "/admin/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("allRoles", allRoles);
        return "add-user";
    }
    @PostMapping(value = "/admin/add")
    public String addUser(@ModelAttribute User user) {
        userService.addUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUserForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isUserAdmin(authentication)) {
            return "redirect:/access-denied";
        }
        User user = userService.getUserById(id);
        List<Role> allRoles= roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);
        return "edit-user";
    }

    @PostMapping(value="/admin/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUserSubmit(@PathVariable("id") Long id,@ModelAttribute("user") User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isUserAdmin(authentication)) {
            return "redirect:/access-denied";
        }
        user.setId(id);
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

    private String validateUser(UserDetails user) {
        if(user.getUsername() == null || user.getUsername().isEmpty()) {
            return "Username cannot be empty";
        }
        if(user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Password cannot be empty";
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
