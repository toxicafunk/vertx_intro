package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

public class PostgresVerticle extends AbstractVerticle {

  private SQLClient client;

  @Override
  public void start() {

    JsonObject postgreSQLClientConfig = new JsonObject()
      .put("host", "172.17.0.3")
      .put("username", "postgres")
      .put("password", "secret")
      .put("database", "invoices");

    client = PostgreSQLClient.createShared(vertx, postgreSQLClientConfig);

    vertx.eventBus().consumer("services.docs.post", message -> {
      client.getConnection(res -> {
        if (res.succeeded()) {
          Doc doc = ((JsonObject) message.body()).mapTo(Doc.class);
          String update = String.format("INSERT INTO docs (mdname, typed) VALUES ('%s', '%s')", doc.getMdname(), doc.getTyped());
          SQLConnection connection = res.result();
          System.out.printf("PSQL: %s\n", update);
          connection.update(update, insResult -> {
            if (insResult.succeeded()) {
              UpdateResult result = insResult.result();
              System.out.printf("Updated %s row with key: %s\n", result.getUpdated(), result.getKeys());
            }
          });
        } else {
          System.out.println("PSQL: Something went awfully wrong!\n");
        }
      });
    });

  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    client.close();
  }
}
