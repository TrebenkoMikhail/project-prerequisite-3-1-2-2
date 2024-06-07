package habsida.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/user").setViewName("user");
        registry.addViewController("/admin").setViewName("admin");
        registry.addViewController("/admin/add").setViewName("add-user");
        registry.addViewController("/admin/edit").setViewName("edit-user");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/registerAdmin").setViewName("registerAdmin");
        registry.addViewController("/login").setViewName("login");
    }
}
