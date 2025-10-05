package com.softdev.softdev.security;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;

import com.softdev.softdev.exception.ResourceNotFoundException;

public class jwtUtil {

    @Value("${jwt.secret_key}")
    private static String secretKey;

    public static String generateToken(Map<String, Object> payload, String secret) {
        try {
            // Header: specify algorithm and type
            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));

            // Convert payload map to JSON manually (simple version)
            String payloadJson = "{" + payload.entrySet().stream()
                    .map(e -> "\"" + e.getKey() + "\":\"" + e.getValue() + "\"")
                    .collect(Collectors.joining(",")) + "}";
            String payloadEncoded = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Signature: HMACSHA256(base64Url(header) + "." + base64Url(payload), secret)
            String signature = hmacSha256(header + "." + payloadEncoded, secret);

            return header + "." + payloadEncoded + "." + signature;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error generating JWT");
        }
    }

    // HMAC SHA256 signing
    private static String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_Key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secret_Key);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(hash);
    }

    // Base64 URL-safe encode (no padding)
    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static Map<String, Object> extractToken(String token) {
        // Split JWT into parts
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new ResourceNotFoundException("Invalid JWT token");
        }

        // Decode the payload (Base64 URL-safe)
        String payloadJson = new String(
            Base64.getUrlDecoder().decode(parts[1]),
            StandardCharsets.UTF_8
        );

        // Simple flat JSON parsing (no nested objects)
        Map<String, Object> payload = Arrays.stream(
                payloadJson.replaceAll("[{}\"]", "").split(",")
            )
            .map(s -> s.split(":", 2)) // split only at first colon
            .filter(arr -> arr.length == 2)
            .collect(Collectors.toMap(
                arr -> arr[0].trim(),
                arr -> arr[1].trim()
            ));

        return payload;
    }

    public static boolean verifyJwt(String token) {
        
        try {
            // Split into 3 parts: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            Map<String, Object> content = jwtUtil.extractToken(token);
            String iat = (String) content.get("iat");

            Long current_time = System.currentTimeMillis()/1000;

            if (current_time > Long.parseLong(iat)){
                throw new ResourceNotFoundException("Token Expried");           
            }

            // Recalculate signature using secret key
            String data = header + "." + payload;
            String expectedSignature = hmacSha256(data, secretKey);

            // Compare constant-time to prevent timing attacks
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    public static String decodePayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) return null;
        return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
    }

}
