package com.example.java_test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

/**
 * 인스턴스는, 테스트간의 의존성을 없애기 위해서 기본 전략으로 매 테스트마다 새로 생성
 * 따라서 테스트는 매번 순서대로 실행 X, 순서가 정해져 있지 않음
 * 5부터는 클래스당 하나만 만들어서 공유할 수 있는 방법이 있음 => BEFORE/AFTER ALL static 일 필요가 없어짐
 */

//상태 정보 공유 + 순차적 실행 보장( ex) 시나리오 테스트)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyTest {

    @Order(2)
    @DisplayName("스터디 생성")
    @FastTest
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

    @Order(1)
    @SlowTest
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

    //테스트 반복
    @DisplayName("스터디 만들기")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetition}")
    void repeatTest(RepetitionInfo repetitionInfo){
        System.out.println("test" + repetitionInfo.getCurrentRepetition() + "/" +
                repetitionInfo.getTotalRepetitions());
    }

    //테스트 반복 : 단일 파라미터
    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @ValueSource(ints = {10, 20, 30})
    void parameterizedTestSingle(@ConvertWith(StudyConverter.class) Study study){
        System.out.println(study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter{   //하나의 인자에만 적용

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "can only convert to study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    //테스트 반복 : 복수 파라미터
    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @CsvSource({"10, '자바 스터디'", "20, '스프링'"})
    void parameterizedTestMultiple(@AggregateWith(StudyAggregator.class) Study study){
        System.out.println(study);
    }

    static class StudyAggregator implements ArgumentsAggregator {

        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        }
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