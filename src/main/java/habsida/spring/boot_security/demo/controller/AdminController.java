package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final ResourceLoader resourceLoader;


    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository, ResourceLoader resourceLoader) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/home")
    public ResponseEntity<String> getAdminPage(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user != null) {
            String userHtml = generateAdminHtml(user);
            return ResponseEntity.ok(userHtml);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    private String generateAdminHtml(User user) {
        return "<tr>" +
                "<td>" + user.getId() + "</td>" +
                "<td>" + user.getFirstname() + "</td>" +
                "<td>" + user.getLastname() + "</td>" +
                "<td>" + user.getAge() + "</td>" +
                "<td>" + user.getEmail() + "</td>" +
                "<td>" + user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", ")) + "</td>" +
                "</tr>";
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/allUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok().body("{\"success\": true}");
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.ok().body("{\"success\": true}");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().body("{\"success\": true}");
    }
}
