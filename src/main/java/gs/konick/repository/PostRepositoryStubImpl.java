package gs.konick.repository;

import gs.konick.model.Post;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Stub
@Repository
public class PostRepositoryStubImpl implements PostRepository {

    private final Set<Post> posts = ConcurrentHashMap.newKeySet();

    public PostRepositoryStubImpl() {
        posts.add(new Post(1, "Test1"));
        posts.add(new Post(2, "Test2"));
        posts.add(new Post(3, "Test3"));
    }

    public Set<Post> all() {
        return posts;
    }

    public Optional<Post> getById(long id) {
        return posts.stream().filter(post -> post.getId() == id).findFirst();
    }

    public Post save(Post post) {
        posts.add(post);
        return post;
    }

    public void removeById(long id) {
        posts.removeIf(post -> post.getId() == id);
    }
}
