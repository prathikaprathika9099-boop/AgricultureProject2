package com.example.AgriProject.service;

import com.example.AgriProject.entity.Order;
import com.example.AgriProject.entity.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
@Service
public class OrderExcelExportService {

    public byte[] exportOrdersToExcel(List<Order> orders) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Orders");

            // ===== HEADER =====
            String[] cols = {
                    "OrderId", "OrderDate", "Status",
                    "UserId", "UserName",
                    "FullName", "Phone", "Street", "City", "State", "Pincode",
                    "ProductName", "Quantity", "Price", "ItemTotal",
                    "ItemsTotal", "ShippingFee", "TotalAmount"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            int rowNum = 1;

            // ===== DATA =====
            for (Order order : orders) {

                log.info("Processing Order ID: {}", order.getId());

                List<OrderItem> items = order.getItems();

                if (items == null || items.isEmpty()) {
                    Row row = sheet.createRow(rowNum++);
                    writeRow(row, order, null);
                    continue;
                }

                for (OrderItem item : items) {
                    Row row = sheet.createRow(rowNum++);
                    writeRow(row, order, item);
                }
            }

            // ===== COLUMN WIDTHS =====
            int[] widths = {
                    5000, 7000, 4000,
                    5000, 7000,
                    7000, 5000, 10000, 5000, 4000, 4000,
                    7000, 3000, 5000, 5000,
                    5000, 5000, 5000
            };

            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i]);
            }

            // ===== OUTPUT =====
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            log.error("❌ EXPORT FAILED", e);
            throw new RuntimeException(e);
        }
    }

    // =========================
    // WRITE ROW (SAFE)
    // =========================
    private void writeRow(Row row, Order order, OrderItem item) {

        int col = 0;

        // ===== ORDER DATA =====
        set(row, col++, order.getId());
        set(row, col++, safe(order.getOrderDate()));
        set(row, col++, safe(order.getStatus()));

        set(row, col++, order.getUser() != null ? order.getUser().getId() : "");
        set(row, col++, order.getUser() != null ? order.getUser().getUsername() : "");

        if (order.getAddress() != null) {
            set(row, col++, safe(order.getAddress().getFullName()));
            set(row, col++, safe(order.getAddress().getPhone()));
            set(row, col++, safe(order.getAddress().getStreet()));
            set(row, col++, safe(order.getAddress().getCity()));
            set(row, col++, safe(order.getAddress().getState()));
            set(row, col++, safe(order.getAddress().getPincode()));
        } else {
            col += 6;
        }

        // ===== ITEM DATA =====
        if (item != null) {

            Double price = item.getPrice() != null ? item.getPrice() : 0.0;
            Integer qty = item.getQuantity() != null ? item.getQuantity() : 0;

            set(row, col++, safe(item.getProductName()));
            set(row, col++, qty);
            set(row, col++, price);

            double itemTotal = price * qty;
            set(row, col++, itemTotal);

        } else {
            col += 4;
        }

        // ===== TOTALS =====
        set(row, col++, safe(order.getItemsTotal()));
        set(row, col++, safe(order.getShippingFee()));
        set(row, col++, safe(order.getTotalAmount()));
    }

    // =========================
    // NULL SAFE VALUE
    // =========================
    private Object safe(Object value) {
        return value != null ? value : "";
    }

    // =========================
    // CELL SETTER
    // =========================
    private void set(Row row, int col, Object value) {

        Cell cell = row.createCell(col);

        if (value == null) {
            cell.setCellValue("");
            return;
        }

        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
