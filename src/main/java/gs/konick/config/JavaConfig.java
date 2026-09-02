package gs.konick.config;

import gs.konick.controller.PostController;
import gs.konick.repository.PostRepository;
import gs.konick.service.PostService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaConfig {
    @Bean
    // название метода - название бина
    public PostController postController(PostService service) {
        // вызов метода и есть DI
        return new PostController(service);
    }

    @Bean
    public PostService postService(PostRepository repository) {
        return new PostService(repository);
    }

    @Bean
    public PostRepository postRepository() {
        return new PostRepository();
    }
}