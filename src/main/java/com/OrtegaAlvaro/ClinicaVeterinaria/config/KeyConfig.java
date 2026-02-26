package com.OrtegaAlvaro.ClinicaVeterinaria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Configuración de claves RSA para la firma y verificación de tokens JWT.
 * Carga un par de claves (pública y privada) desde un Java KeyStore (.jks).
 * - La clave PRIVADA se usa para FIRMAR los tokens.
 * - La clave PÚBLICA se usa para VERIFICAR los tokens.
 */
@Configuration
public class KeyConfig {

    @Value("${jwt.keystore.path}")
    private String keystorePath;

    @Value("${jwt.keystore.password}")
    private String keystorePassword;

    @Value("${jwt.keystore.alias}")
    private String keystoreAlias;

    /**
     * Crea un bean que carga el par de claves (privada y pública) desde el
     * keystore.
     *
     * @return KeyPair con la clave privada y pública.
     * @throws Exception Si ocurre un error al cargar el keystore.
     */
    @Bean
    public KeyPair jwtKeyPair() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keyStore.load(fis, keystorePassword.toCharArray());
        }

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(keystoreAlias, keystorePassword.toCharArray());
        PublicKey publicKey = keyStore.getCertificate(keystoreAlias).getPublicKey();

        return new KeyPair(publicKey, privateKey);
    }
}
