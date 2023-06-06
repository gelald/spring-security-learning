package com.github.gelald.oauth2;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WuYingBin
 * date: 2023/6/6
 */
@SpringBootTest
public class AuthApplicationTests {
    /**
     * 使用私钥生成JWT令牌
     */
    @Test
    public void testGenerateJwt() {
        //证书文件名称
        String keystore = "key.jks";
        //密钥库文件密码
        String keyStorePass = "gelald@123";
        //密钥库文件的位置
        ClassPathResource resource = new ClassPathResource(keystore);
        //密钥库别名
        String alias = "gelald";
        //密钥访问密码
        String keyPass = "gelald@123";
        //创建密钥工厂
        KeyStoreKeyFactory keyFactory = new KeyStoreKeyFactory(
                resource, keyStorePass.toCharArray());
        //获取密钥对（私钥和公钥）
        KeyPair keyPair = keyFactory.getKeyPair(alias, keyPass.toCharArray());
        //从密钥对中获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //构建JWT令牌的内容
        Map<String, Object> body = new HashMap<>();
        body.put("name", "yanchengzhi");
        body.put("address", "湖北");
        body.put("age", 25);
        body.put("state", "single");
        //map转成JSON串
        String bodyString = JSON.toJSONString(body);
        //使用私钥生成JWT令牌
        Jwt jwt = JwtHelper.encode(bodyString, new RsaSigner(privateKey));
        //获取JWT令牌
        String encode = jwt.getEncoded();
        System.out.println(encode);
    }


    /**
     * 使用公钥校验JWT令牌
     */
    @Test
    public void testMatchJwt() {
        //jwt令牌，上面生成的直接复制过来
        String jwtString = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZGRyZXNzIjoi5rmW5YyXIiwibmFtZSI6InlhbmNoZW5nemhpIiwic3RhdGUiOiJzaW5nbGUiLCJhZ2UiOjI1fQ.DWVMI2KDA9LUcrT-rvltoVKjUUZaeCVn8_HCYvRCjR2sWm8Qou04ECOjbZIYLU8PQpyXZsC8Mi7VH9O7UPMiu_QWKOsTk7j21Qmn01Fh1SsDQ2NiH3O1Xhf-p-YfUlO0n7YEQR3OkUW7Ri6GXlmir9NPBCb_kWgRa3W9BcUULp63KSYfgyhq9yysDHsI9FiMiQdduMn3qkdlZCpCV4l5xEwoMuewyRuXqX-eY98NbD-iWDFF2g5lk4AnVVQhpIGdihef8aldrFH_PphDttL9WG3v0hg8dxeSYKwD-2ueJ6pFxMzYraqmd3su0mNaeVc1h_DuNzuwVbWJG3oG0z7_Pw";
        //公钥文件名称
        String publicKeyName = "public-key.txt";
        //获取公钥资源
        ClassPathResource resource = new ClassPathResource(publicKeyName);
        try {
            //获取资源文件
            File file = resource.getFile();
            //字符输入流
            Reader reader = new FileReader(file);
            char[] chs = new char[(int) file.length()];
            //读取
            int len = reader.read(chs);
            //获取公钥内容
            String publicKey = new String(chs, 0, len);
            //校验JWT
            Jwt jwt = JwtHelper.decodeAndVerify(jwtString, new RsaVerifier(publicKey));
            //获取jwt令牌的内容
            String claims = jwt.getClaims();
            System.out.println(claims);
            //关闭流
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
