package com.example.java_test;

import org.junit.jupiter.api.*;

class StudyTest {
    @Test
    @DisplayName("스터디 생성")
    void create() {
        Study study = new Study();
    }

    //테스트 시작 전 한번 실행
    @BeforeAll
    static void beforeAll(){
        System.out.println("before all the test");
    }

    @AfterAll
    static void afterAll(){
        System.out.println("after all the test");
    }

    //각 테스트 실행 전마다 실행
    @BeforeEach
    void beforeEach(){
        System.out.println("before the test");
    }

    @AfterEach
    void afterEach(){
        System.out.println("after the test");
    }
}