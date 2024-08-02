package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public ResponseEntity<String> getUserPage(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user != null) {
            String userHtml = generateUserHtml(user);
            return ResponseEntity.ok(userHtml);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    private String generateUserHtml(User user) {
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
}
