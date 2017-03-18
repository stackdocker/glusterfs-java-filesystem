package objectstack.web;

import objectstack.util.Runner;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

import java.util.ArrayList;

/**
 * This is an example application to showcase the usage of Vert.x Web.
 *
 * In this application you will see the usage of:
 *
 *  * Thymeleaf templates
 *  * Vert.x Web
 *
 * @author <a href="mailto:pmlopes@gmail.com>Paulo Lopes</a>
 */
public class ServerVerticle extends AbstractVerticle {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runExample(ServerVerticle.class);
  }

  @Override
  public void start() throws Exception {

    // To simplify the development of the web components we use a Router to route all HTTP requests
    // to organize our code in a reusable way.
    final Router router = Router.router(vertx);

    // In order to use a Thymeleaf template we first need to create an engine
    final ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create();

    router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET).
    		allowedMethod(HttpMethod.POST).allowedMethod(HttpMethod.PUT).
    		allowedMethod(HttpMethod.DELETE));
    
    // Entry point to the application, this will render a custom JADE template.
    router.get().handler(ctx -> {
      String name = this.config().getString("name", "Hi there!");
      ctx.put("welcome", name);

      ctx.put("redirectUrl", "http://172.17.4.200:9099/storage?redirect=http%3A%2F%2F172%2E17%2E4%2E200%3A9098");
      ctx.data().put("files", new ArrayList<String>());
      
      // and now delegate to the engine to render it.
      engine.render(ctx, "templates/index.html", res -> {
        if (res.succeeded()) {
          ctx.response().end(res.result());
        } else {
          ctx.fail(res.cause());
        }
      });
    });

   // start a HTTP web server on port 8080
    vertx.createHttpServer().requestHandler(router::accept).listen(9080, "0.0.0.0");
  }
}