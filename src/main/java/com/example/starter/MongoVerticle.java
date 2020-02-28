package com.example.starter;

import io.reactivex.Flowable;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import io.vertx.reactivex.core.streams.ReadStream;
import io.vertx.reactivex.ext.mongo.MongoClient;

import java.util.ArrayList;


public class MongoVerticle extends AbstractVerticle {
  @Override
  public void start() {

    String address = config().getString("address");
    System.out.println("Deployed service for " + address);

    JsonObject config = new JsonObject()
      .put("host", "172.17.0.2")
      .put("port", 27017)
      .put("db_name", "invoices");

    MongoClient client = MongoClient.createShared(vertx, config);

    vertx.eventBus().consumer("services.docs.get", message -> {

      /*JsonObject query = new JsonObject()
      .put("mdname", "test3");*/

      ReadStream<JsonObject> docs = client.findBatch("docs", new JsonObject());

      Flowable<JsonObject> flowable = docs.toFlowable();

      flowable
        .collect(ArrayList::new, ArrayList::add)
        .subscribe(list -> {
          message.reply(new JsonArray(list));
        });

    });

    vertx.eventBus().consumer("services.docs.post", message -> {
      JsonObject doc = (JsonObject) message.body();
      client.save("docs", doc, res -> {
        if (res.succeeded()) {
          System.out.printf("MongoDB: %s\n", res.result());
        }
      });
    });
  }
}
