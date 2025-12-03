package cl.ucn.pipefilter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PricingFilterVerticle extends AbstractVerticle
{
    @Override
    public void start(Promise<Void> startPromise)
    {
        vertx.eventBus().consumer("order.validated", msg -> {

            JsonObject json = (JsonObject) msg.body();

            //lista de pedidos
            long subtotal = 0;
            JsonArray jsonArray = json.getJsonArray("items");

            for (Object obj : jsonArray)
            {
                JsonObject item = (JsonObject) obj;
                subtotal += (item.getInteger("quantity") * item.getLong("unitPrice"));
            }

            long discount = 0;
            String coupon = json.getString("couponCode", "");

            switch(coupon)
            {
                case "DESCUENTO10":
                    subtotal = (long) (subtotal * 0.10);
                    break;
                case "DESCUENTO20":
                    subtotal = (long) (subtotal * 0.20);
                    break;
                default:
                    break;
            }

            long total = subtotal - discount;

            json.put("subtotal", subtotal);
            json.put("discount", discount);
            json.put("total", total);
            json.put("status", "CALCULADA");

            System.out.println("nuevo total: " + subtotal);
            vertx.eventBus().send("order.priced", json);

        });
        startPromise.complete();
    }

}
