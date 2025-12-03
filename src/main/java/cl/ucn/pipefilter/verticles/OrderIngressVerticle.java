package cl.ucn.pipefilter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

public class OrderIngressVerticle extends AbstractVerticle
{
    @Override
    public void start(Promise<Void> startPromise)
    {
        vertx.setTimer(1000, id ->{

            JsonObject order = new JsonObject()
                    .put("orderId", "ORD-1001")
                    .put("customerId", "CUST-1")
                    .put("couponCode", "DESCUENTO10")
                    .put("currency", "CLP")
                    .put("paymentMethod", "WEBPAY")
                    .put("timestamp", Instant.now().toString())
                    .put("items", new JsonArray()
                            .add(new JsonObject().put("productId", "LIBRO-JAVA").put("quantity", 2).put("unitPrice", 15000))
                            .add(new JsonObject().put("productId", "TAZA-UCN").put("quantity", 1).put("unitPrice", 5000))
                    );
            vertx.eventBus().send("order.raw", order);
        });
    }
}
