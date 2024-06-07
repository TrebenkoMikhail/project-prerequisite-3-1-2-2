package habsida.spring.boot_security.demo.service;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {
    List<User> getAllUsers();
    User getUserById(Long id);
    void saveUser(User user);
    void saveAdmin(User user);

    void addUser(User user, String role);

    void updateUser(User user);
    void deleteUserById(Long id);
    UserDetails loadUserByUsername(String username);
    User findByUsername(String username);

    Set<Role> getUserRolesById(Long userId);

}
