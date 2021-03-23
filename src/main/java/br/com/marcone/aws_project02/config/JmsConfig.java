package br.com.marcone.aws_project02.config;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${aws.sqs.queue.product_events.name}")
    private String queueName;

    @Value("${aws.region}")
    private String awsRegion;

    private SQSConnectionFactory sqsFactory;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(){
        sqsFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build());

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(sqsFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("2");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        return factory;
    }

}
