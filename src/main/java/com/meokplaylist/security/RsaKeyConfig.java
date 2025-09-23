package com.meokplaylist.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.InputStream;

@Configuration
public class RsaKeyConfig {

    @Value("${rsa.private-key-path}")
    private String privateKeyPath;

    @Value("${rsa.public-key-path}")
    private String publicKeyPath;

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        byte[] keyBytes = readKeyBytes(privateKeyPath);
        String privateKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        byte[] keyBytes = readKeyBytes(publicKeyPath);
        String publicKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    private byte[] readKeyBytes(String path) throws IOException {
        InputStream is;
        if (path.startsWith("/")) {
            // 운영 환경: 파일 시스템 경로 (예: /etc/secrets/private.pem)
            is = new FileInputStream(path);
        } else {
            // 로컬 환경: classpath 리소스 (예: classpath:keys/private.pem)
            is = new ClassPathResource(path).getInputStream();
        }
        return is.readAllBytes();
    }
}