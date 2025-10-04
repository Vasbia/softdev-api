package com.softdev.softdev.security;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class jwtUtil {
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
            throw new RuntimeException("Error generating JWT", e);
        }
    }

    // HMAC SHA256 signing
    private static String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(hash);
    }

    // Base64 URL-safe encode (no padding)
    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static void main(String[] args) {
        Map<String, Object> payload = Map.of(
                "sub", "pipatpong",
                "gmail", "pooh@gmail.com",
                "user_id", "12345",
                "iat", System.currentTimeMillis() / 1000
        );
        String secret = "MY_SUPER_SECRET_KEY";
        String token = generateToken(payload, secret);
        System.out.println(token);
    }

    public static Map<String, Object> extractToken(String token) {
        // Split JWT into parts
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
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
}
