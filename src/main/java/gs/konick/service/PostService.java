package gs.konick.service;

import gs.konick.exception.NotFoundException;
import gs.konick.logger.FileLogger;
import gs.konick.logger.Logger;
import gs.konick.model.Post;
import gs.konick.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PostService {
    private final PostRepository repository;
    private final AtomicLong counter = new AtomicLong(1);

    private final Logger logger;

    public PostService(PostRepository repository) {
        this.repository = repository;
        this.logger = new FileLogger(this.getClass().getSimpleName());
    }

    public Set<Post> all() {
        return repository.all();
    }

    public Post getById(long id) {
        logger.info("Getting post by id: " + id);
        return repository.getById(id).orElseThrow(NotFoundException::new);
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(getNextId());
        }
        logger.info("Saving post: " + post);
        return repository.save(post);
    }

    public void removeById(long id) {
        logger.info("Removing post: " + id);
        if (repository.getById(id).isPresent()) {
            repository.removeById(id);
        } else {
            logger.warn("Post not found: " + id);
        }
    }

    private long getNextId() {
        while (repository.getById(counter.get()).isPresent()) {
            counter.incrementAndGet();
        }
        return counter.get();
    }
}