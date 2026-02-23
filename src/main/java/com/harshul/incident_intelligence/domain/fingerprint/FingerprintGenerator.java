package com.harshul.incident_intelligence.domain.fingerprint;

import com.harshul.incident_intelligence.dto.IncidentRequestDTO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringJoiner;

public final class FingerprintGenerator {

    private static final String VERSION = "v1";

    private FingerprintGenerator() {
        // Prevent instantiation
    }

    public static String generate(IncidentRequestDTO request) {

        StringJoiner joiner = new StringJoiner("|");

        joiner.add(VERSION);
        joiner.add(normalize(request.getServiceName()));
        joiner.add(normalize(request.getFailureType()));
        joiner.add(normalize(request.getRootCauseClass()));
        joiner.add(normalize(request.getErrorCode()));

        return sha256(joiner.toString());
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return value.trim().toLowerCase();
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}