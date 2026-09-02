package gs.konick.service;

import gs.konick.exception.NotFoundException;
import gs.konick.logger.FileLogger;
import gs.konick.logger.Logger;
import gs.konick.model.Post;
import gs.konick.repository.PostRepository;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class PostService {
    private final PostRepository repository;
    private final AtomicLong counter = new AtomicLong();

    private final Logger logger;

    public PostService(PostRepository repository) {
        this.repository = repository;
        this.logger = new FileLogger(this.getClass().getSimpleName());

        save(new Post(1, "Test1"));
        save(new Post(2, "Test2"));
        save(new Post(3, "Test3"));
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
            post.setId(counter.incrementAndGet());
        }
        logger.info("Saving post: " + post);
        return repository.save(post);
    }

    public void removeById(long id) {
        logger.info("Removing post: " + id);
        repository.removeById(id);
    }
}