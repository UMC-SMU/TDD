## 기본 애노테이션

### **@Test**  
### **@BeforeAll / @AfterAll**  

: 모든 테스트 전 한 번만 실행한다.    
: 접근 제한자는 private 불가능, default 가능이다. 또한 return은 없어야 한다. 즉 static void 로 선언하면 된다고 보면 된다.  

### **@BeforeEach / @AfterEach**  

: 각 테스트 후 실행한다.  

### **@Disabled**  

: 이 테스트는 실행하지 않는다.(좋은 방법은 아니다)  

## 테스트 이름 표기

### **@DisplayNameGeneration**

: 어떻게 DisplayName을 생성할 것인지 전략을 표현할 때 사용한다. 이 애노테이션은 클래스, 메소드에 사용할 수 있다.   
: 기본 구현체로 ReplaceUnderscores 제공한다.

### **@DisplayName**  
: 어떤 테스트인지 테스트 이름을 쉽게 표현할 수 있는 방법을 제공한다. 위의 @DisplayNameGeneration보다 우선 순위가 높다.

[테스트 이름 관련 문서](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-names)



## Assertion

### **assertEquals**

아래와 같이 expected, actual, 에러 message 순으로 작성할 수 있다.  
```java
assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태 값이 DRAFT여야 한다.");
```
message에서 문자열만 넘기는 경우에는 테스트가 성공 여부에 관계없이 항상 문자열 연산(+연산)을 실행하지만 람다식은 실패할 시에만 실행한다.
즉, **람다식을 사용**하는게 성능상으로 유리할 수 있다.  

![image](https://user-images.githubusercontent.com/50178026/162112465-61373900-5259-46bb-affc-6f2e14de1b2f.png)

### **assertNotNull(actual)**

값이 null이 아닌지 확인한다.

### **assertTrue(boolean)**

다음 조건이 참인지 확인한다.

### **assertAll(executables...)**

모든 확인 구문 확인한다.

```java
        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(), ()-> "스터디를 처음 만들면 상태 값이 DRAFT여야 한다."),
                () -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 인원은 0보다 커야 한다.")
        );
```
한 테스트 내에서 하나의 테스트가 실패하게 된다면, 다음 테스트는 무시가 되는데 assertAll을 사용할 경우 모든 테스트의 결과를 반영해 준다.

### **assertThrows(expectedType, executable)**

예외 발생을 확인한다.
```java
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit는 0보다 커야 한다.", ex.getMessage());
```
결과 값을 리턴받아 에러 메시지와 비교할 수도 있다.


### **assertTimeout(duration, executable)** 

특정 시간 안에 실행이 완료되는지 확인한다.

```java
        assertTimeout(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300); 
        });
```
위의 예제대로 실행을 한다면 100ms가 넘더라도 실제 코드가 종료되어야 테스트가 종료된다. 따라서 특정 시간이 지나면 테스트가 즉시 종료될 수 있는 `assertTimeoutPreemptively`를 사용할 수 있다.    
하지만 `assertTimeoutPreemptively`을 사용할 때 코드 블럭을 사용할 경우, 코드 블럭은 별도의 스레드에서 실행하기에 예상치 못한 결과가 발생할 수 있다. 즉 Thread와 관련없는 경우에만 사용해야 한다. 

## 조건에 따른 테스트
- **`org.junit.jupiter.api.Assumptions.*`**
  - assumeTrue(조건)
     ```java
        //조건
        Assumptions.assumeTrue("LOCAL".equalsIgnoreCase(test_env));
        //만족하면 테스트
        Study actual = new Study(10);
        org.assertj.core.api.Assertions.assertThat(actual.getLimit()).isGreaterThan(0);
     ```
  - assumingThat(조건, 테스트)
      ```java

        //조건, 테스트
        Assumptions.assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            Study actual2 = new Study(10);
            org.assertj.core.api.Assertions.assertThat(actual2.getLimit()).isGreaterThan(0);
        });
      ```
- **애노테이션 사용**
  - **@Enabled___ 와 @Disabled___**
    ```java
        //실행할 운영체제 
        @EnabledOnOs({OS.MAC, OS.LINUX})
        //실행할 java 버전
        @EnabledOnJre({JRE.JAVA_8})
        
        //무시할 운영체제 선택
        @DisabledOnOs(OS.MAC)
    ```

## 테스트 반복
- **@RepeatedTest**
  ```java
    @DisplayName("스터디 만들기 테스트")
    @RepeatedTest(value = 5, name = "{displayName}, {currentRepetition}/{totalRepetitions}") // (1)
    // (2)
    void repeatTeat(RepetitionInfo repetitionInfo){
        System.out.println("test" + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
    }
  ```
  - (1) 스터디 만들기 테스트, 1/5 처럼 이름 지정 가능
  - (2) repetitionInfo 인자를 통해 현재 횟수를 알 수 있다.
- **@ParameterizedTest** 
   ```java
    @ParameterizedTest(name = "{index} {displayName} {0}") // (1)
    @ValueSource(strings = {"날씨가", "많이", "더워지고", "있당"}) // (2)
    void parameterizedTest(String message){
        System.out.println(message);
    }
   ```
   - (1)파라미터 참고 가능  
   - (2)파라미터만큼 반복 실행 가능

## 인자 반복 자세히

### **@NullSource, @EmptySource, @NullAndEmptySource**
  - 빈 문자열 인자로 전달


### 인자 값 타입 변환

```java
    @DisplayName("하이용")
    @ParameterizedTest(name = "{displayName}")
    @ValueSource(ints = {10, 40, 30})
    void parameterizedTest(@ConvertWith(StudyConverter.class) String message){
        System.out.println(message);
    }

    //형변환
    static class StudyConverter extends SimpleArgumentConverter{

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }
```
- `SimpleArgumnetConverter`를 구현해서 형 변환을 한다.


### CSV

```java
    @DisplayName("csv 테스트")
    @ParameterizedTest(name = "{displayName} {index}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void parameterizedTestUsingCsv(Integer limit, String name){
        System.out.println(new Study(limit, name));
    }
    
    //Study{status=null, limit=20, name='스프링'} 출력
```

### ArgumentAccessor
: 인자 값을 조합하는 방식 

```java
    @DisplayName("argumentAccessor 테스트")
    @ParameterizedTest(name = "{displayName} {index}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void parameterizedTest_argumentsAccessor(ArgumentsAccessor argumentsAccessor){
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        System.out.println(study);
    }
    
    //Study{status=null, limit=10, name='자바 스터디'}
```

또한 위의 코드를 아래와 같이 구현을 통해 줄일 수 있다.

```java
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
```

## JUnit 테스트 인스턴스

JUnit은 테스트 별로 새로 인스턴스를 만들기에 서로 공유하는 값을 변경하지 않도록 한다.  
이 기본 전략을 `@TestInstance(Lifecycle.PER_CLASS)`을 통해 클래스 당 인스턴스 하나를 만들어서 변경할 수 있다.

1. beforeAll, afterAll인 경우 static 생략 가능
2. 전역 변수 공유 가능

## JUnit 순서

JUnit은 서로 간의 의존성이 없도록 하기 위해 테스트 순서는 언제든 변경이 될 수 있다. 하지만 원하는 순서에 따라 테스트를 진행하는 방식을 제공한다. 

테스트 클래스에 `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`을 붙이고 각 테스트에 `@Order(1)`을 붙이면 된다.

## JUnit5 설정 파일

`src/test/resources/` 하단에 `junit-platform.properties` 추가

- `junit.jupiter.testinstance.lifecycle.default = per_class`
    - 클래스 별로 붙였던 조건을 전체적으로 적용 가능

- `junit.jupiter.extensions.autodetection.enabled = true`
    - 확장팩 자동 감지

- `junit.jupiter.conditions.deactivate = org.junit.*DisabledCondition`
    - `@disabled` 무시
    
- `Display`전략
    - `junit.jupiter.displayname.generator.default = org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscore`
    - `_`을 공백으로 변경한다.
  


