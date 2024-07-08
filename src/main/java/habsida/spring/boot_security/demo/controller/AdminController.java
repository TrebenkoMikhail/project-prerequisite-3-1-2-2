package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping(value = "/admin")
    public String adminPage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User user = userService.findByUsername(currentUser.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("activeTab", "admin");
        return "admin";
    }
    @GetMapping(value ="/admin/allUsers")
    private String adminAllUsers(Model model, @AuthenticationPrincipal UserDetails currentUser){
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("user", new User());
        model.addAttribute("activeTab", "allUsers");
        return "admin-allUsers";
    }

    @GetMapping(value = "/admin/add")
    public String showAddForm(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("activeTab", "new-user");
        return "admin-allUsers";
    }
    @PostMapping(value = "/admin/add")
    public String addUser(@ModelAttribute User user) {
        userService.addUser(user);
        return "redirect:/admin/allUsers";
    }

    @GetMapping("/admin/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUserForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isUserAdmin(authentication)) {
            return "redirect:/access-denied";
        }
        User user = userService.getUserById(id);
        List<Role> allRoles= roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);
        model.addAttribute("activeTab", "editUserModal");
        return "admin-allUsers";
    }

    @PostMapping(value="/admin/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUserSubmit(@PathVariable("id") Long id,@ModelAttribute("user") User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isUserAdmin(authentication)) {
            return "redirect:/access-denied";
        }
        user.setId(id);
        userService.updateUser(user);
        return "redirect:/admin/allUsers";
    }

    @GetMapping(value = "/admin/delete/{id}")
    public String deleteFormUserById(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("activeTab", "deleteModal");
        return "admin-allUsers";
    }

    @DeleteMapping(value = "/admin/delete/{id}")
    public String deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/allUsers";
    }
    private boolean isUserAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return true;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
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
