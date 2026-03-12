package com.example.AgriProject.controller;

import com.example.AgriProject.entity.Order;
import com.example.AgriProject.repository.OrderRepository;
import com.example.AgriProject.service.OrderExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminOrderExportController {

    private final OrderRepository orderRepository;
    private final OrderExcelExportService excelExportService;

    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/admin/orders/export")
    public ResponseEntity<byte[]> exportNewOrdersExcel() {

        List<Order> orders = orderRepository.findByPrintedFalseOrderByOrderDateDesc();

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        byte[] excelBytes = excelExportService.exportOrdersToExcel(orders);

        //orders.forEach(o -> o.setPrinted(true));
        //orderRepository.saveAll(orders);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .body(excelBytes);
    }

    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/admin/orders/export/by-date")
    public ResponseEntity<byte[]> exportNewOrdersByDate(@RequestParam("date") String date) {

        LocalDate localDate = LocalDate.parse(date);

        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = localDate.atTime(LocalTime.MAX);

        List<Order> orders =
                orderRepository.findByPrintedFalseAndOrderDateBetweenOrderByOrderDateDesc(start, end);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        byte[] excelBytes = excelExportService.exportOrdersToExcel(orders);

        // mark as printed
        //orders.forEach(o -> o.setPrinted(true));
        //orderRepository.saveAll(orders);

        String fileName = "orders_" + date + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .body(excelBytes);
    }

}
