package com.locdata.location.test;

import com.locdata.location.LocationApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LocationApplication.class)
public class LocationApplicationTests {

	@Value("${local.server.port}")
	private int port;
	private String URL;

	// Topic for websocket communication
	private static final String WEBSOCKET_TOPIC = "/topic/location/";

	// WebSocket end point
	private static final String WEBSOCKET_ENDPOINT = "/locationendpoint";

	private CompletableFuture<String> completableFuture;

	@Before
	public void setup() {
		completableFuture = new CompletableFuture<>();
		URL = "ws://localhost:" + port + WEBSOCKET_ENDPOINT;
	}

	@Test
	public void testLocationEndpoint() throws URISyntaxException, InterruptedException,
			ExecutionException, TimeoutException {

		WebSocketStompClient stompClient
				= new WebSocketStompClient(new SockJsClient(createTransportClient()));
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
		}).get(1, SECONDS);

		stompSession.subscribe(WEBSOCKET_TOPIC, new LocationStompFrameHandler());
		stompSession.send(WEBSOCKET_TOPIC, "Test Data");

		String locationData = completableFuture.get(10, SECONDS);

		assertNotNull(locationData);
	}

	private List<Transport> createTransportClient() {
		List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		return transports;
	}

	private class LocationStompFrameHandler implements StompFrameHandler {
		@Override
		public Type getPayloadType(StompHeaders stompHeaders) {
			return String.class;
		}

		@Override
		public void handleFrame(StompHeaders stompHeaders, Object o) {
			completableFuture.complete((String) o);
		}
	}
}