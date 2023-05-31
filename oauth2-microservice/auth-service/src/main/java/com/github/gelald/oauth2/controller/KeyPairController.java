package com.github.gelald.oauth2.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * 获取RSA公钥接口
 * <br/>
 * <a href="https://blog.csdn.net/DreamsArchitects/article/details/109674979">生成密钥对</a>
 * <br/>
 * <a href="https://blog.csdn.net/weixin_51954021/article/details/112758486">nimbus-jose-jwt的使用</a>
 *
 * @author WuYingBin
 * date: 2023/5/31
 */
@RestController
public class KeyPairController {

    @Autowired
    private KeyPair keyPair;

    @GetMapping("/rsa/publicKey")
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }
}
