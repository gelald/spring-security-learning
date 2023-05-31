package com.github.gelald.oauth2.filter;

import cn.hutool.json.JSONObject;
import com.github.gelald.oauth2.properties.GatewaySecureProperties;
import com.github.gelald.oauth2.utils.EncryptAndDecrypt;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Slf4j
@Component
public class EncryptFilter implements GlobalFilter, Ordered {

    @Autowired
    private GatewaySecureProperties gatewaySecureProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (gatewaySecureProperties.getDev() || PassUrl.isPassUrl(exchange.getRequest().getURI().toString())) {
                    return chain.filter(exchange);
                }
                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                    DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                    DataBuffer join = dataBufferFactory.join(dataBuffers);
                    byte[] content = new byte[join.readableByteCount()];
                    join.read(content);
                    DataBufferUtils.release(join);
                    // 流转为字符串
                    String responseData = new String(content, StandardCharsets.UTF_8);
                    if (responseData.contains("data")) {
                        JSONObject jsonObject = new JSONObject(responseData);
                        Object data = jsonObject.get("data");
                        String encryptData = EncryptAndDecrypt.encryptAES2(data.toString());
                        jsonObject.set("data", encryptData);
                        responseData = jsonObject.toString();
                    }
                    byte[] uppedContent = responseData.getBytes(StandardCharsets.UTF_8);
                    originalResponse.getHeaders().setContentLength(uppedContent.length);
                    return bufferFactory.wrap(uppedContent);
                }));
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
        return chain.filter(exchange.mutate().response(response).build());
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}