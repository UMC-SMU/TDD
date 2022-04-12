package com.example.java_test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

class StudyTest {
    @Test
    @DisplayName("스터디 생성")
    void create() {
        Study study = new Study(10);

        assertNotNull(study);
        assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 DRAFT 여야 한다.");

        //테스트가 깨지는 것을 한번에 알기
        assertAll(
                () ->assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(),
                        () -> "스터디를 처음 만들면 상태값이 DRAFT 여야 한다.")

        );

        //예외 처리
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit은 0보다 커야 한다.", exception.getMessage());

        //시간 설정
        assertTimeout(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(20);
        });
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기")
    @EnabledOnOs(OS.WINDOWS)   //특정 OS
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11})   //특정 자바 버전
    void doTestByFilter1(){
        String test_env = System.getenv("TEST_ENV");

        assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            System.out.println("local");
            Study actual = new Study(100);
            assertThat(actual.getLimit()).isGreaterThan(0);
        });
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
    void doTestByFilter2(){
        assumeTrue("LOCAL".equalsIgnoreCase(System.getenv("TEST_ENV")));
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