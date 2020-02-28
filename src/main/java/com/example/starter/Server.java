package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class Server extends AbstractVerticle {

  @Override
  public void start() {
    HttpServer server = vertx.createHttpServer();
    Router router = configRoutes();

    server.requestHandler(router).listen(8080);
  }

  protected Router configRoutes() {
    Router router = Router.router(vertx);
    router.get("/api/:entityName").handler(this::getEntities);
    router.post("/api/:entityName").handler(this::addEntity);

    return router;
  }

  private void getEntities(RoutingContext routingContext) {
    String entityName = routingContext.pathParam("entityName").toLowerCase();

    vertx.eventBus().request("services." + entityName + ".get", null, message -> {
      if (message.succeeded()) {
        routingContext.response()
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encode(message.result().body()));
      }
    });
  }

  private void addEntity(RoutingContext routingContext) {
    String entityName = routingContext.pathParam("entityName").toLowerCase();
    HttpServerRequest req = routingContext.request();
    req.bodyHandler(buffer -> {
      JsonObject doc = buffer.toJsonObject();
      vertx.eventBus().publish("services." + entityName + ".post", doc);
      routingContext.response()
        .putHeader("Content-Type", "application/json; charset=utf-8")
        .end(doc.encode());
    });

  }
}

