package gs.konick.repository;

import gs.konick.model.Post;

import java.util.Optional;
import java.util.Set;

public interface PostRepository {

    Set<Post> all();

    Optional<Post> getById(long id);

    Post save(Post post);

    void removeById(long id);
}