package com.example.javatest;

import org.junit.jupiter.api.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class StudyTest {

    @Test
    @DisplayName("스터디 만들기 ✏️️")
    public void create(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit는 0보다 커야 한다.", ex.getMessage());

        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300); //실제로 오래 걸리는 코드가 테스트에 있다면 테스트 자체가 오래 걸린다.
        });
    }

    @Test
    public void create1() throws Exception{
        System.out.println("create1");
    }

    @BeforeAll //모든 테스트 전 한 번 실행, static void (private X, default O,return X, )
    static void beforeAll() {
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("after all");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before each");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After each");
        System.out.println();
    }
}