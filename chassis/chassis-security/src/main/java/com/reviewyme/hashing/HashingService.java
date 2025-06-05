package com.reviewyme.hashing;

public interface HashingService {
    String hash(String data);
    boolean matches(String rawData, String hashedData);
}