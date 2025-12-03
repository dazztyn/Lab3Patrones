package cl.ucn.pipefilter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class FraudCheckFilterVerticle extends AbstractVerticle
{
    @Override
    public void start(Promise<Void> startPromise)
    {
        vertx.eventBus().consumer("order.priced", msg -> {

            JsonObject json = (JsonObject) msg.body();
            long total = json.getLong("total");
            String paymentMethod = json.getString("paymentMethod");
            int cantItems = json.getJsonArray("items").size();

            if(total > 200000 && "TARJETA_CREDITO".equals(paymentMethod))
            {
                json.put("status", "REVISION");
                System.out.println("Actividad sospechosa en orden " + json.getString("orderId"));
            }
            else if(cantItems > 20)
            {
                json.put("status", "REVISION");
                System.out.println("Actividad sospechosa en orden " + json.getString("orderId"));
            }
            else
            {
                System.out.println("Orden " + json.getString("orderId") + " procesada sin sospechas");
            }

            vertx.eventBus().send("order.persist", json);

        });
        startPromise.complete();
    }
}
