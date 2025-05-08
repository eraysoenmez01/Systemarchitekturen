package at.fhv.sysarch.lab2.ordermanager.service;

import at.fhv.sysarch.lab2.ordermanager.Order;
import at.fhv.sysarch.lab2.ordermanager.catalog.ProductCatalog;
import io.grpc.stub.StreamObserver;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    //kilian fragen?????!!!===????


    @Override
    public void placeOrder(Order.OrderRequest request, StreamObserver<Order.OrderReply> responseObserver) {
        String name = request.getName();
        int amount = request.getAmount();

        double unitPrice = ProductCatalog.getUnitPrice(name);
        double totalPrice = unitPrice * amount;

        Order.Item item = Order.Item.newBuilder()
                .setName(name)
                .setAmount(amount)
                .setPrice(unitPrice)
                .build();

        Order.OrderReply reply = Order.OrderReply.newBuilder()
                .addItems(item)
                .setTotalPrice(totalPrice)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}