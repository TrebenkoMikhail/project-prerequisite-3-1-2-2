package habsida.spring.boot_security.demo.controller;

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
import java.util.*;
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

    @GetMapping("/api/admin/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping(value = "/allUsers", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getUserDetailsHtml(Authentication authentication) throws IOException {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Resource resource = resourceLoader.getResource("classpath:templates/admin-allUsers.html");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String allUsersHtml = new String(fileData, StandardCharsets.UTF_8);

        if (user != null) {
            allUsersHtml = allUsersHtml.replace("${username}", user.getUsername());
            allUsersHtml = allUsersHtml.replace("${roles}", user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));
        }

        List<User> allUsers = userService.getAllUsers();
        StringBuilder userRows = new StringBuilder();

        for (User u : allUsers) {
            userRows.append("<tr>")
                    .append("<td>").append(u.getId()).append("</td>")
                    .append("<td>").append(u.getFirstname()).append("</td>")
                    .append("<td>").append(u.getLastname()).append("</td>")
                    .append("<td>").append(u.getAge()).append("</td>")
                    .append("<td>").append(u.getEmail()).append("</td>")
                    .append("<td>").append(u.getRoles().stream().map(Role::getName).collect(Collectors.joining(", "))).append("</td>")
                    .append("<td><button class='btn btn-primary edit' data-id='").append(u.getId()).append("'>Edit</button></td>")
                    .append("<td><button class='btn btn-danger delete' data-id='").append(u.getId()).append("'>Delete</button></td>")
                    .append("</tr>");
        }

        allUsersHtml = allUsersHtml.replace("<!--USERS_DATA-->", userRows.toString());

        return ResponseEntity.ok(allUsersHtml);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        Set<Role> roles = user.getRoles().stream()
                .map(role -> roleRepository.findByName(role.getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (roles.size() != user.getRoles().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"success\": false, \"message\": \"One or more roles are invalid\"}");
        }
        user.setRoles(roles);
        userService.addUser(user);

        return ResponseEntity.ok().body("{\"success\": true}");
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User existingUser = userService.getUserById(id);
        if (existingUser != null) {
            userService.updateUser(updatedUser);
            return ResponseEntity.ok().body(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body("User successfully deleted!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("id", user.getId());
            userDetails.put("firstname", user.getFirstname());
            userDetails.put("lastname", user.getLastname());
            userDetails.put("age", user.getAge());
            userDetails.put("email", user.getEmail());
            userDetails.put("username", user.getUsername());
            userDetails.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

}
