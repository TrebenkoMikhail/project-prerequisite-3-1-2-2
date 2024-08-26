package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/home")
    public ResponseEntity<String> getAdminHomePage(Authentication authentication) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/user.html");
            byte[] fileUserData = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String homeUserHtml = new String(fileUserData, StandardCharsets.UTF_8);

            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user != null) {
                homeUserHtml = homeUserHtml.replace("${username}", user.getUsername());
                homeUserHtml = homeUserHtml.replace("${roles}", user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));

                String userHtml = generateUserHtml(user);
                homeUserHtml = homeUserHtml.replace("<!--USER_DATA-->", userHtml);
            }

            return new ResponseEntity<>(homeUserHtml, HttpStatus.OK);
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


}
