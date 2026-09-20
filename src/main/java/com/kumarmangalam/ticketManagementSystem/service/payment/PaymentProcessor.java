package com.kumarmangalam.ticketManagementSystem.service.payment;

public class PaymentProcessor {
    PaymentService paymentService;

    public PaymentProcessor(PaymentService paymentService){
        this.paymentService = paymentService;
    }
    public boolean pay(){
        paymentService.processPayment();
        return true;
    }
}
