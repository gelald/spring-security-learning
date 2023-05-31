package com.github.gelald.oauth2.filter;

import com.github.gelald.oauth2.properties.GatewaySecureProperties;
import com.github.gelald.oauth2.utils.EncryptAndDecrypt;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Slf4j
@Component
public class DecryptFilter implements GlobalFilter, Ordered {

    @Autowired
    private GatewaySecureProperties gatewaySecureProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 路径带参请求解密
        String url = exchange.getRequest().getURI().toString();
        // 判断是否需要不进行解密
        if (this.gatewaySecureProperties.getDev() || PassUrl.isPassUrl(url)) {
            return chain.filter(exchange);
        }
        HttpMethod method = exchange.getRequest().getMethod();

        int i = url.lastIndexOf("/");
        if (i != -1) {
            String prefixUrl = url.substring(0, i);
            String urlAndParam = url.substring(i + 1);
            String realUrl;
            String white = whiteUrl(url);
            if (white != null) {
                String[] split = url.split(white);
                realUrl = split[0] + white + (split.length > 1 ? EncryptAndDecrypt.decryptAES2(split[1].replaceAll("/", "")) : "");
            } else {
                realUrl = prefixUrl + EncryptAndDecrypt.decryptAES2(urlAndParam);
            }
            log.info("decrypted url: {}", realUrl);
            ServerHttpRequest request = exchange.getRequest().mutate().uri(URI.create(realUrl)).build();
            exchange = exchange.mutate().request(request).build();
        }
        //Post请求解密
        if ("POST".equals(method.toString())) {
            String s = this.resolveBodyFromRequest(exchange.getRequest());
            if (s != null) {
                URI uri = exchange.getRequest().getURI();
                URI newUri = UriComponentsBuilder.fromUri(uri).build(true).toUri();
                ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();
                String realData;
                if (s.contains("{") && s.contains("}")) {
                    realData = s;
                } else {
                    realData = EncryptAndDecrypt.decryptAES2(s.replaceAll("\"", ""));
                }
                DataBuffer buffer = StringBuffer(realData);
                Flux<DataBuffer> bodyFlux = Flux.just(buffer);

                request = new ServerHttpRequestDecorator(request) {

                    @Override
                    public Flux<DataBuffer> getBody() {
                        return bodyFlux;
                    }
                };
                request = request.mutate()
                        // String的长度与DataBuffer长度不一致,最终反序列化的时候是根据DataBuffer长度来定的
                        .header(HttpHeaders.CONTENT_LENGTH, Integer.toString(buffer.capacity()))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();

                return chain.filter(exchange.mutate().request(request).build());
            }
        }
        return chain.filter(exchange);
    }

    private NettyDataBuffer StringBuffer(String realData) {
        byte[] bytes = realData.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory factory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        NettyDataBuffer buffer = factory.allocateBuffer(bytes.length);
        return buffer.write(bytes);
    }

    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     *
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();

        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            bodyRef.set(charBuffer.toString());
            DataBufferUtils.release(buffer);
        });
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //获取request body
        return bodyRef.get();
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private String whiteUrl(String url) {
        List<String> urls = this.gatewaySecureProperties.getIgnoreUrls();
        for (String white : urls) {
            white = white.replaceAll("/\\*\\*", "");
            if (url.contains(white)) {
                return white;
            }
        }
        return null;
    }

}