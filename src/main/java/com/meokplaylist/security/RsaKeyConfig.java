package com.meokplaylist.security;

import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;

@Configuration
public class RsaKeyConfig {


    @Value("classpath:keys/private.pem")
    private Resource privatePem;

    @Value("classpath:keys/public.pem")
    private Resource publicPem;

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        String pem = Files.readString(privatePem.getFile().toPath())
                .replaceAll("-----\\w+ PRIVATE KEY-----|\\s", "");
        RSAPrivateKey key = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));

        return key;
    }

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String pem = Files.readString(publicPem.getFile().toPath())
                .replaceAll("-----\\w+ PUBLIC KEY-----|\\s", "");
        RSAPublicKey key = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(pem)));
        return key;
    }
}

