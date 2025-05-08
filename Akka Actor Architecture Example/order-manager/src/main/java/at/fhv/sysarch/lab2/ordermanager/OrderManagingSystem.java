package at.fhv.sysarch.lab2.ordermanager;

import at.fhv.sysarch.lab2.ordermanager.service.OrderServiceImpl;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class OrderManagingSystem {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090)
                .addService((BindableService) new OrderServiceImpl())
                .build();

        server.start();
        server.awaitTermination();
    }
}