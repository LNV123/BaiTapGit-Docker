package fit.hutech.spring;

import fit.hutech.spring.entities.Book;
import fit.hutech.spring.entities.Category;
import fit.hutech.spring.entities.Role;
import fit.hutech.spring.entities.User;
import fit.hutech.spring.repositories.IBookRepository;
import fit.hutech.spring.repositories.ICategoryRepository;
import fit.hutech.spring.repositories.IRoleRepository;
import fit.hutech.spring.repositories.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;

@Configuration
public class AppConfig {

        @Bean
        public CommandLineRunner initData(IBookRepository bookRepository,
                        ICategoryRepository categoryRepository,
                        IRoleRepository roleRepository,
                        IUserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
                return args -> {
                        // Initialize Roles if not exist
                        if (roleRepository.count() == 0) {
                                roleRepository.save(Role.builder().id(1L).name("ADMIN")
                                                .description("Administrator role").build());
                                roleRepository.save(
                                                Role.builder().id(2L).name("USER").description("User role").build());
                        }

                        // Create Admin User if not exist
                        if (userRepository.findByUsername("admin").isEmpty()) {
                                Role adminRole = roleRepository.findRoleById(1L);
                                User admin = User.builder()
                                                .username("admin")
                                                .password(passwordEncoder.encode("admin"))
                                                .email("admin@hutech.vn")
                                                .roles(new HashSet<>(Collections.singletonList(adminRole)))
                                                .build();
                                userRepository.save(admin);
                        }

                        // Initialize Categories and Books
                        if (categoryRepository.count() == 0) {
                                Category category = Category.builder()
                                                .name("Công nghệ thông tin")
                                                .build();
                                categoryRepository.save(category);

                                if (bookRepository.count() == 0) {
                                        bookRepository.save(Book.builder()
                                                        .title("Lập trình Web Spring Framework")
                                                        .author("Ánh Nguyễn")
                                                        .price(29.99)
                                                        .category(category)
                                                        .build());

                                        bookRepository.save(Book.builder()
                                                        .title("Lập trình ứng dụng Java")
                                                        .author("Huy Cường")
                                                        .price(45.63)
                                                        .category(category)
                                                        .build());

                                        bookRepository.save(Book.builder()
                                                        .title("Lập trình Web Spring Boot")
                                                        .author("Xuân Nhân")
                                                        .price(12.0)
                                                        .category(category)
                                                        .build());

                                        bookRepository.save(Book.builder()
                                                        .title("Lập trình Web Spring MVC")
                                                        .author("Ánh Nguyễn")
                                                        .price(0.12)
                                                        .category(category)
                                                        .build());
                                }
                        }
                };
        }
}
