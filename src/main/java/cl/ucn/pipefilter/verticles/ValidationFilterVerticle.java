package cl.ucn.pipefilter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ValidationFilterVerticle extends AbstractVerticle
{

    //funcion para verificar que el json recibido tenga todas las keys con la info necesaria
    public boolean mustHaveCamps(JsonObject json)
    {
        if(!json.containsKey("orderId") ||  json.getString("orderId").isEmpty()) return false;
        if(!json.containsKey("customerId") ||  json.getString("customerId").isEmpty()) return false;
        if(!json.containsKey("items") || json.getString("items").isEmpty()) return false;
        if(!json.containsKey("currency")) return false;
        if(!json.containsKey("paymentMethod")) return false;
        if(!json.containsKey("timestamp")) return false;
        return true;
    }

    public boolean itemRules(JsonObject json)
    {
        JsonArray items = json.getJsonArray("items");
        if(items == null || items.isEmpty()) return false;

        for(Object i: items)
        {
            JsonObject item = (JsonObject) i;
            if (!item.containsKey("productId") || item.getString("productId").isEmpty())
            {
                return false;
            }

            Integer quantity = item.getInteger("quantity");
            Long price = item.getLong("unitPrice");

            if (quantity == null || quantity <= 0) return false;
            if (price == null || price < 0) return false;
        }
        return true;
    }


    @Override
    public void start(Promise<Void> startPromise)
    {
        vertx.eventBus().consumer("order.raw", msg -> {

            JsonObject json = (JsonObject) msg.body();

            if(mustHaveCamps(json) & itemRules(json))
            {
                System.out.println("Orden valida, enviando.");
                vertx.eventBus().send("order.validated", json);
            }
            else
            {
                System.out.println("Orden invalida, eliminando.");
                vertx.eventBus().send("order.error", json);
            }

        });
        startPromise.complete();
    }
}
