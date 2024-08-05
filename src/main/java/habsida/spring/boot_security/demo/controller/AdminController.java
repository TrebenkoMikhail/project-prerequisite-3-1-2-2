package habsida.spring.boot_security.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<String> getHomePage(Authentication authentication) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/admin.html");
            byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String homeHtml = new String(fileData, StandardCharsets.UTF_8);

            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user != null) {
                homeHtml = homeHtml.replace("${username}", user.getUsername());
                homeHtml = homeHtml.replace("${roles}", user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));

                String userHtml = generateUserHtml(user);
                homeHtml = homeHtml.replace("<!--USER_DATA-->", userHtml);
            }

            return new ResponseEntity<>(homeHtml, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error loading home page", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateUserHtml(User user) {
        return "<tr>" +
                "<td>" + user.getId() + "</td>" +
                "<td>" + user.getFirstname() + "</td>" +
                "<td>" + user.getLastname() + "</td>" +
                "<td>" + user.getAge() + "</td>" +
                "<td>" + user.getEmail() + "</td>" +
                "<td>" + user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")) + "</td>" +
                "</tr>";
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping(value = "/allUsers", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getUserDetailsHtml(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("<h1>401 Unauthorized</h1>");
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("<h1>404 Not Found</h1>");
        }

        List<User> allUsers = userService.getAllUsers();
        StringBuilder htmlResponse = new StringBuilder("<html><body><h1>All Users</h1><table class=\"table\"><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Age</th><th>Email</th><th>Role</th></tr>");

        for (User u : allUsers) {
            htmlResponse.append("<tr>")
                    .append("<td>").append(u.getId()).append("</td>")
                    .append("<td>").append(u.getFirstname()).append("</td>")
                    .append("<td>").append(u.getLastname()).append("</td>")
                    .append("<td>").append(u.getAge()).append("</td>")
                    .append("<td>").append(u.getEmail()).append("</td>")
                    .append("<td>").append(u.getRoles().stream().map(Role::getName).collect(Collectors.joining(", "))).append("</td>")
                    .append("</tr>");
        }
        htmlResponse.append("</table></body></html>");

        return ResponseEntity.ok(htmlResponse.toString());
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
