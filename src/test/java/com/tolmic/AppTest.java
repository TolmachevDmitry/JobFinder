package com.tolmic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;

@SpringBootTest
public class AppTest 
{

    @Autowired
    private EmbeddingModel EmbeddingModel;

    @Test 
    void test() {
        assertTrue(true);
    } 
}
