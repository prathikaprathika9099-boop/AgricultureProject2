package com.example.AgriProject.events;

import com.example.AgriProject.entity.Order;
import com.example.AgriProject.repository.OrderRepository;
import com.example.AgriProject.service.OrderExcelExportService;
import com.example.AgriProject.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderPlacedListener {
    private final OrderRepository orderRepository;
    private final WhatsAppService whatsAppService;
    private final OrderExcelExportService excelOrderExporter;


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event){
        try {
            Order order = orderRepository.findByIdWithItems(event.orderId())
                    .orElseThrow(() -> new RuntimeException("Order not found: " + event.orderId()));


            try {
                whatsAppService.sendOrderConfirmation(order);
            } catch (Exception e) {
               System.out.println("Error in whatsapp service");
            }

        } catch (Exception e) {
            System.out.println("Error in excel sheet");
        }
    }
}
