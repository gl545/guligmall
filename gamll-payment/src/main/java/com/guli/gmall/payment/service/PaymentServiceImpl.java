package com.guli.gmall.payment.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.guli.gmall.bean.PaymentInfo;
import com.guli.gmall.mq.ActiveMQUtil;
import com.guli.gmall.payment.mapper.PaymentInfoMapper;
import com.guli.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        paymentInfoMapper.updateByOutTradeNo(paymentInfo);
        //更新完毕给订单发送消息队列
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAYMENT_RESULT_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            TextMessage textMessage = session.createTextMessage(paymentInfo.getOrderSn());
            producer.send(textMessage);
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (JMSException e) {
                try {
                    session.rollback();
                } catch (JMSException jmsException) {
                    jmsException.printStackTrace();
                }
                e.printStackTrace();
            }
        }

    }
}
