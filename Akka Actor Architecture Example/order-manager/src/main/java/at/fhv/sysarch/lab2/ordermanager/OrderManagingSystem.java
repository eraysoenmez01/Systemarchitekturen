package at.fhv.sysarch.lab2.ordermanager;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.settings.ServerSettings;
import akka.japi.function.Function;
import at.fhv.sysarch.lab2.ordermanager.service.OrderServiceImpl;

import java.util.concurrent.CompletionStage;

public class OrderManagingSystem {
    public static void main(String[] args) {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "OrderSystem");

        OrderService service = new OrderServiceImpl(system);

        Function<HttpRequest, CompletionStage<HttpResponse>> handler =
                OrderServiceHandlerFactory.createWithServerReflection(service, system);

        // Configure server with HTTP/2 support
        ServerSettings serverSettings = ServerSettings.create(system.classicSystem())
                .withHttp2Enabled(true);  // Enable HTTP/2

        Http.get(system)
                .newServerAt("127.0.0.1", 50051)
                .withSettings(serverSettings)
                .bind(handler)
                .thenAccept(binding -> {
                    System.out.println("gRPC server bound to: " + binding.localAddress());
                })
                .exceptionally(ex -> {
                    System.err.println("Server binding failed: " + ex.getMessage());
                    ex.printStackTrace();
                    system.terminate();
                    return null;
                });

        system.getWhenTerminated().toCompletableFuture().join();
    }
}