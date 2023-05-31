#生成 JKS Java KeyStore 文件
keytool -genkeypair -alias gelald -keyalg RSA -keypass gelald@123 -keystore key.jks -storepass gelald@123

keytool -importkeystore -srckeystore key.jks -destkeystore key.jks -deststoretype pkcs12

#导出公钥
keytool -list -rfc --keystore key.jks | openssl x509 -inform pem -pubkey