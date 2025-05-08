package at.fhv.sysarch.lab2.ordermanager.service;

import at.fhv.sysarch.lab2.ordermanager.*;
import at.fhv.sysarch.lab2.ordermanager.catalog.ProductCatalog;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class OrderServiceImpl implements OrderService {

    @Override
    public CompletionStage<OrderReply> placeOrder(OrderRequest request) {
        String name = request.getName();
        int amount = request.getAmount();
        double unitPrice = ProductCatalog.getUnitPrice(name);
        double totalPrice = unitPrice * amount;

        Item item = Item.newBuilder()
                .setName(name)
                .setAmount(amount)
                .setPrice(unitPrice)
                .build();

        OrderReply reply = OrderReply.newBuilder()
                .addItems(item)
                .setTotalPrice(totalPrice)
                .build();

        return CompletableFuture.completedFuture(reply);
    }
}