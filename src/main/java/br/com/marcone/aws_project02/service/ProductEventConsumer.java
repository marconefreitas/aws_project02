package br.com.marcone.aws_project02.service;

import br.com.marcone.aws_project02.model.Envelope;
import br.com.marcone.aws_project02.model.ProductEvent;
import br.com.marcone.aws_project02.model.ProductEventLog;
import br.com.marcone.aws_project02.model.SnsMessage;
import br.com.marcone.aws_project02.repository.ProductEventLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Service
public class ProductEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);

    private ObjectMapper mapper;
    private ProductEventLogRepository logRepository;

    @Autowired
    public ProductEventConsumer(ObjectMapper mapper, ProductEventLogRepository logRepository){
        this.mapper = mapper;
        this.logRepository = logRepository;
    }

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProductEvent(TextMessage text) throws JMSException, IOException {

        SnsMessage snsMessage = mapper.readValue(text.getText(), SnsMessage.class);
        Envelope env = mapper.readValue(snsMessage.getMessage(), Envelope.class);

        ProductEvent productEvent = mapper.readValue(env.getData(), ProductEvent.class);

        log.info("Product event message. Event: {}  - Product id: {} - MessageID {} - ",
                    env.getEventType(),
                    productEvent.getProductId(),
                    snsMessage.getMessageId());

        ProductEventLog productEventLog = buildProductEventLog(env, productEvent, snsMessage.getMessageId());
        logRepository.save(productEventLog);
    }

    private ProductEventLog buildProductEventLog(Envelope env, ProductEvent productEvent, String messageId){
        ProductEventLog productEventLog = new ProductEventLog();
        long timestamp = Instant.now().toEpochMilli();

        productEventLog.setPk(productEvent.getCode());
        productEventLog.setSk(env.getEventType() + "_" + timestamp);
        productEventLog.setEvent(env.getEventType());
        productEventLog.setProductId(productEvent.getProductId());

        productEventLog.setTimestamp(timestamp);
        productEventLog.setTtl(Instant.now().plus(Duration.ofMinutes(10)).getEpochSecond() );
        productEventLog.setUsername(productEvent.getUsername());
        productEventLog.setMessageId(messageId);
        return productEventLog;


    }

}
