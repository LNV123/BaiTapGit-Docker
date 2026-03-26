package fit.hutech.spring.utils;

import fit.hutech.spring.entities.Category;
import fit.hutech.spring.repositories.ICategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final ICategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        List<String> categoryNames = List.of(
                "Tiểu thuyết", "Khoa học viễn tưởng", "Kinh tế",
                "Công nghệ thông tin", "Văn học", "Truyện tranh", "Tâm lý học",
                "Kỹ năng sống", "Ngoại ngữ", "Lịch sử");

        for (String name : categoryNames) {
            if (categoryRepository.findByName(name).isEmpty()) {
                categoryRepository.save(Category.builder().name(name).build());
                System.out.println("Đã thêm danh mục: " + name);
            }
        }
    }
}
