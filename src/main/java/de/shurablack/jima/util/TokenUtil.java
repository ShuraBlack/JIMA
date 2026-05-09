package de.shurablack.jima.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

/**
 * Utility class for creating PKCS12 KeyStore files for encrypted token storage.
 *
 * <p><b>Purpose:</b>
 * TokenUtil provides functionality to create PKCS12-format KeyStore files that securely store
 * API tokens in an encrypted format. Tokens are stored as AES-encrypted secret key entries
 * protected by a password-based KeyStore.
 *
 * <p><b>Example Usage:</b>
 * <pre>
 * String token = "sk_live_1234567890...";
 * TokenUtil.createStore("myPassword123", Arrays.asList(token));
 * // Creates/overwrites "tokens.store" PKCS12 file
 * </pre>
 *
 * @see AppSettings
 */
public class TokenUtil {

    private static final Logger LOGGER = LogManager.getLogger(TokenUtil.class);

    private TokenUtil() {}

    /**
     * Creates a PKCS12 KeyStore file containing encrypted API tokens.
     *
     * <p>This method creates or overwrites a "tokens.store" file in the current working directory.
     * Each token from the input list is stored as a password-protected AES-encrypted secret key
     * entry with sequential naming (token_0, token_1, token_2, etc.).
     *
     * <p><b>File Output:</b>
     * Creates or overwrites "tokens.store" in the current working directory with the specified tokens.
     *
     * <p><b>Error Handling:</b>
     * All exceptions are caught and logged individually:
     * <ul>
     *   <li>{@link KeyStoreException} - KeyStore creation or entry setup failures</li>
     *   <li>{@link FileNotFoundException} - Cannot write to tokens.store location</li>
     *   <li>{@link CertificateException} - Certificate/cryptographic errors</li>
     *   <li>{@link IOException} - General I/O errors during KeyStore operations</li>
     *   <li>{@link NoSuchAlgorithmException} - AES algorithm not available</li>
     * </ul>
     *
     * @param password The password used to protect the KeyStore and encrypt tokens.
     *                 Should be a strong, non-empty string. Recommended: 16+ alphanumeric characters.
     * @param tokens   List of API tokens to store. Each token is stored as a SecretKey with
     *                 index-based naming: token_0, token_1, token_2, etc.
     *                 Empty list is allowed but results in empty KeyStore.
     *
     * @see javax.crypto.spec.SecretKeySpec
     */
    public static void createStore(String password, List<String> tokens) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, password.toCharArray());

            for (int i = 0; i <= tokens.size() - 1; i++) {
                KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(
                    new SecretKeySpec(tokens.get(i).getBytes(), "AES")
                );
                KeyStore.PasswordProtection passwordProtection =
                    new KeyStore.PasswordProtection(password.toCharArray());
                keyStore.setEntry("token_" + i, secret, passwordProtection);
            }

            keyStore.store(new FileOutputStream("tokens.store"), password.toCharArray());
            LOGGER.info("Successfully created KeyStore with {} tokens", tokens.size());
        } catch (KeyStoreException e) {
            LOGGER.error("Failed to create KeyStore: {}", e.getMessage());
        } catch (FileNotFoundException e) {
            LOGGER.error("KeyStore file not found: {}", e.getMessage());
        } catch (CertificateException e) {
            LOGGER.error("Certificate error while creating KeyStore: {}", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("I/O error while creating KeyStore: {}", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Algorithm not found while creating KeyStore: {}", e.getMessage());
        }
    }
}
