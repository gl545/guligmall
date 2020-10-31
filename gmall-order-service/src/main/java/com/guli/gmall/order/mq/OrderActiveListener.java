package com.guli.gmall.order.mq;

import com.guli.gmall.bean.OmsOrder;
import com.guli.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
public class OrderActiveListener {

    @Autowired
    OrderService orderService;

    @JmsListener(destination = "PAYMENT_RESULT_QUEUE",containerFactory = "jmsQueueListener")
    public void updateProcessStatus(TextMessage textMessage) throws JMSException {
        String orderSn = textMessage.getText();
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(orderSn);
        omsOrder.setStatus(1);
        orderService.updateProcessStatus(omsOrder);
    }
}
