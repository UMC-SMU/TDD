package com.example.javatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
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
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyTest {

    int value = 0;

    @Test
    @DisplayName("스터디 만들기 ✏️️")
    public void create(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit는 0보다 커야 한다.", ex.getMessage());

        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(10); //실제로 오래 걸리는 코드가 테스트에 있다면 테스트 자체가 오래 걸린다.
        });
    }

    @Test
    @Order(1)
    public void create1() throws Exception{
        System.out.println("create1");
        System.out.println(value++);
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

    @Test
    @DisplayName("조건 테스트")
    @EnabledOnOs({OS.MAC, OS.LINUX})
    void condition_test(){
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);

        //조건
        Assumptions.assumeTrue("LOCAL".equalsIgnoreCase(test_env));
        //만족하면 테스트
        Study actual = new Study(10);
        org.assertj.core.api.Assertions.assertThat(actual.getLimit()).isGreaterThan(0);

        //조건
        Assumptions.assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            Study actual2 = new Study(10);
            org.assertj.core.api.Assertions.assertThat(actual2.getLimit()).isGreaterThan(0);
        });
    }

    @DisplayName("스터디 만들기 테스트")
    @RepeatedTest(value = 5, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    @Order(2)
    void repeatTeat(RepetitionInfo repetitionInfo){
        System.out.println(value++);
        System.out.println("test" + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("하이용")
    @ParameterizedTest(name = "{displayName}")
    @ValueSource(ints = {10, 40, 30})
    void parameterizedTest(@ConvertWith(StudyConverter.class) Study study){
        System.out.println(study);
    }

    //형변환
    static class StudyConverter extends SimpleArgumentConverter{

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    @DisplayName("csv 테스트")
    @ParameterizedTest(name = "{displayName} {index}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void parameterizedTestUsingCsv(Integer limit, String name){
        System.out.println(new Study(limit, name));
    }

    @DisplayName("argumentAccessor 테스트")
    @ParameterizedTest(name = "{displayName} {index}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void parameterizedTest_argumentsAccessor(@AggregateWith(StudyAggregator.class) Study study){
        System.out.println(study);
    }

    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        }

    }

}