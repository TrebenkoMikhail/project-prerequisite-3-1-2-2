package habsida.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/api/user").setViewName("user");
        registry.addViewController("/api/admin").setViewName("admin-allUsers");
        registry.addViewController("/api/register").setViewName("register");
        registry.addViewController("/api/login").setViewName("login");
    }
}
