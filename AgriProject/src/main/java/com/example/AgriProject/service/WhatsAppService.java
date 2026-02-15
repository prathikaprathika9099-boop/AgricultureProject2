package com.example.AgriProject.service;


import com.example.AgriProject.entity.Order;
import com.example.AgriProject.entity.OrderItem;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String fromNumber;

    public void sendOrderConfirmation(Order order){

        try {
            Twilio.init(accountSid, authToken);

            StringBuilder message = new StringBuilder();
            message.append("\uD83D\uDED2 Order Confirmed!\\n\\n");

            message.append("Items:\n");

            for (OrderItem item : order.getItems()) {
                message.append(item.getProductName())
                        .append(" x ")
                        .append(item.getQuantity())
                        .append(" = ₹")
                        .append(item.getPrice() * item.getQuantity())
                        .append("\n");
            }

            message.append("\nItems Total: ₹")
                    .append(order.getItemsTotal());

            message.append("\nShipping Fee: ₹")
                    .append(order.getShippingFee());

            message.append("\n------------------");
            message.append("\nTotal Amount: ₹")
                    .append(order.getTotalAmount());

            String phone = order.getAddress().getPhone();

            if (phone == null || phone.trim().isEmpty()) {
                throw new RuntimeException("Customer phone number is empty!");
            }

            phone = phone.trim();
            phone = phone.replaceAll("\\s+", ""); // remove spaces
            phone = phone.replace("-", "");

            if (!phone.startsWith("+")) {
                phone = "+91" + phone;
            }

            Message.creator(
                    new PhoneNumber("whatsapp:" + phone),
                    new PhoneNumber("whatsapp:" + fromNumber),
                    message.toString()
            ).create();

        }catch (Exception e){
            System.out.println("Error in whatsapp service: "+e.getMessage());
            e.printStackTrace();
        }
    }
}
