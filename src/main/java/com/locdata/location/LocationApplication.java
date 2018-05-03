package com.locdata.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@Configuration
@EnableAutoConfiguration
@ComponentScan("com.locdata.location")
public class LocationApplication {

    @Autowired
    private SimpMessagingTemplate template;

    @Value( "${cloudmqtt.uri}" )
    private String cloudmqttUri;

    @Value( "${cloudmqtt.user}" )
    private String cloudmqttUser;

    @Value( "${cloudmqtt.pwd}" )
    private String cloudmqttPwd;

    @Value( "${cloudmqtt.clientid}" )
    private String cloudmqttClientId;

    @Value( "${cloudmqtt.topic}" )
    private String cloudmqttTopic;

    private static final String LOCATION_ENDPOINT = "/topic/location";

	public static void main(String[] args) {
		SpringApplication.run(LocationApplication.class, args);
	}

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs(this.cloudmqttUri);
        factory.setUserName(this.cloudmqttUser);
        factory.setPassword(this.cloudmqttPwd);
        return factory;
    }

	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        this.cloudmqttClientId,
                        mqttClientFactory(),
                        this.cloudmqttTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
			    String payload = (String) message.getPayload();
				System.out.println(payload);
                // send it to websocket endpoint
				template.convertAndSend(LOCATION_ENDPOINT, payload);
			}

		};
	}
}
