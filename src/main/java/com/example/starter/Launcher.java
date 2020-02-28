package com.example.starter;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Launcher {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(Server.class.getName());

    String mongoVerticle = MongoVerticle.class.getName();
    String psqlVerticle = PostgresVerticle.class.getName();

    JsonObject options = new JsonObject().put("address", mongoVerticle);
    vertx
      .deployVerticle(mongoVerticle, new DeploymentOptions()
          .setWorker(true)
          .setConfig(options),
        result -> {
          System.out.println("Deployed successfully MongoVerticle");
        });

    options = new JsonObject().put("address", psqlVerticle);
    vertx.deployVerticle(psqlVerticle, new DeploymentOptions()
        .setWorker(true).setConfig(options),
      result -> {
        System.out.println("Deployed successfully PSQLVerticle");
      });
  }

}
