package gs.konick.repository;

import gs.konick.model.Post;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Stub
public class PostRepository {

    private final Set<Post> posts = ConcurrentHashMap.newKeySet();

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