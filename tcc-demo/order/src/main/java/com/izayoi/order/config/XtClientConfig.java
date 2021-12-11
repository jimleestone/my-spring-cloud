package com.izayoi.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

@Configuration
public class XtClientConfig {

//	@Bean
//	public RSocket rSocket() {
//		return RSocketConnector.create().payloadDecoder(PayloadDecoder.ZERO_COPY)
//				.metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.toString())
//				.dataMimeType(WellKnownMimeType.APPLICATION_JSON.toString()).connect(TcpClientTransport.create(7000))
//				.block();
//
//	}

	@Bean
	RSocketStrategies strategies() {
		return RSocketStrategies.builder().encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
				.decoders(decoders -> decoders.add(new Jackson2CborDecoder())).build();
	}

	@Bean
	RSocketRequester requester(RSocketStrategies strategies) {
		return RSocketRequester.builder().rsocketStrategies(strategies()).tcp("localhost", 7000);
	}

}
