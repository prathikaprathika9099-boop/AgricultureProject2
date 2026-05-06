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
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Orders");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Create Header
            Row header = sheet.createRow(0);

            String[] cols = {
                    "OrderId", "OrderDate", "Status",
                    "UserId", "UserName",
                    "FullName", "Phone", "Street", "City", "State", "Pincode",
                    "ProductName", "Quantity", "Price", "ItemTotal",
                    "ItemsTotal", "ShippingFee", "TotalAmount"
            };

            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;

            for (Order order : orders) {

                // Add a blank row BEFORE every new order (except first)
                if (rowNum > 1) {
                    rowNum++;
                }

                if (order.getItems() == null || order.getItems().isEmpty()) {
                    Row row = sheet.createRow(rowNum++);
                    writeOrderRowWithoutItems(row, order);
                    continue;
                }

                for (OrderItem item : order.getItems()) {
                    Row row = sheet.createRow(rowNum++);
                    writeOrderItemRow(row, order, item);
                }
            }


            
int[] columnWidths = {
    5000, // OrderId
    7000, // OrderDate
    4000, // Status
    5000, // UserId
    7000, // UserName
    7000, // FullName
    5000, // Phone
    10000, // Street
    5000, // City
    4000, // State
    4000, // Pincode
    7000, // ProductName
    3000, // Quantity
    5000, // Price
    5000, // ItemTotal
    5000, // ItemsTotal
    5000, // ShippingFee
    5000  // TotalAmount
};

for (int i = 0; i < columnWidths.length; i++) {
    sheet.setColumnWidth(i, columnWidths[i]);
}

            // Convert to byte[]
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            log.error("Failed to export orders to Excel", e);
            throw new RuntimeException("Excel export failed: " + e.getMessage());
        }
    }

    private void writeOrderRowWithoutItems(Row row, Order order) {
        int col = 0;

        set(row, col++, order.getId());
        set(row, col++, order.getOrderDate() != null ? order.getOrderDate().toString() : "");
        set(row, col++, order.getStatus() != null ? order.getStatus().name() : "");

        set(row, col++, order.getUser() != null ? order.getUser().getId() : "");
        set(row, col++, order.getUser() != null ? order.getUser().getUsername() : "");

        if (order.getAddress() != null) {
            set(row, col++, order.getAddress().getFullName());
            set(row, col++, order.getAddress().getPhone());
            set(row, col++, order.getAddress().getStreet());
            set(row, col++, order.getAddress().getCity());
            set(row, col++, order.getAddress().getState());
            set(row, col++, order.getAddress().getPincode());
        } else {
            col += 6;
        }

        // Item columns empty
        set(row, col++, "");
        set(row, col++, "");
        set(row, col++, "");
        set(row, col++, "");

        set(row, col++, order.getItemsTotal());
        set(row, col++, order.getShippingFee());
        set(row, col++, order.getTotalAmount());
    }

    private void writeOrderItemRow(Row row, Order order, OrderItem item) {
        int col = 0;

        set(row, col++, order.getId());
        set(row, col++, order.getOrderDate() != null ? order.getOrderDate().toString() : "");
        set(row, col++, order.getStatus() != null ? order.getStatus().name() : "");

        set(row, col++, order.getUser() != null ? order.getUser().getId() : "");
        set(row, col++, order.getUser() != null ? order.getUser().getUsername() : "");

        if (order.getAddress() != null) {
            set(row, col++, order.getAddress().getFullName());
            set(row, col++, order.getAddress().getPhone());
            set(row, col++, order.getAddress().getStreet());
            set(row, col++, order.getAddress().getCity());
            set(row, col++, order.getAddress().getState());
            set(row, col++, order.getAddress().getPincode());
        } else {
            col += 6;
        }

        set(row, col++, item.getProductName());
        set(row, col++, item.getQuantity());
        set(row, col++, item.getPrice());

        double itemTotal = item.getPrice() * item.getQuantity();
        set(row, col++, itemTotal);

        set(row, col++, order.getItemsTotal());
        set(row, col++, order.getShippingFee());
        set(row, col++, order.getTotalAmount());
    }

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
