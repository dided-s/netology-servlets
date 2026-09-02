package gs.konick.servlet;

import gs.konick.controller.PostController;
import gs.konick.logger.FileLogger;
import gs.konick.logger.Logger;
import gs.konick.repository.PostRepository;
import gs.konick.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;

    private static final String API_POSTS = "/api/posts";
    private static final String API_POST_ID = "/api/posts/\\d+";

    private final Logger logger = new FileLogger(this.getClass().getSimpleName());

    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            String path = req.getRequestURI();
            String method = req.getMethod();

            logger.info("Method: " + method + " " + path);
            // primitive routing
            if (method.equals("GET") && path.equals(API_POSTS)) {
                controller.all(resp);
                return;
            }
            if (method.equals("GET") && path.matches(API_POST_ID)) {
                // easy way
                var id = parseId(path);
                controller.getById(id, resp);
                return;
            }
            if (method.equals("POST") && path.equals(API_POSTS)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals("DELETE") && path.matches(API_POST_ID)) {
                // easy way
                var id = parseId(path);
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private long parseId(String path) {
        int startIndex = path.lastIndexOf("/") + 1;
        String str = path.substring(startIndex);
        return Long.parseLong(str);
    }
}