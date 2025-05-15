package at.fhv.sysarch.lab2.ordermanager.service;

import at.fhv.sysarch.lab2.ordermanager.OrderRequest;
import at.fhv.sysarch.lab2.ordermanager.OrderReply;
import at.fhv.sysarch.lab2.ordermanager.OrderService;
import at.fhv.sysarch.lab2.ordermanager.actors.OrderProcessingActor;
import at.fhv.sysarch.lab2.ordermanager.catalog.ProductCatalog;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class OrderServiceImpl implements OrderService {

    private final ActorSystem<?> system;
    private final ProductCatalog catalog;

    public OrderServiceImpl(ActorSystem<?> system) {
        this.system = system;
        this.catalog = new ProductCatalog();
    }

    @Override
    public CompletionStage<OrderReply> placeOrder(OrderRequest request) {
        CompletableFuture<OrderReply> future = new CompletableFuture<>();

        ActorRef<OrderProcessingActor.PlaceOrder> orderActor = system.systemActorOf(
                OrderProcessingActor.create(catalog, future),
                "order-" + request.getName(),
                akka.actor.typed.Props.empty()
        );

        orderActor.tell(new OrderProcessingActor.PlaceOrder(request, future));
        return future;
    }
}