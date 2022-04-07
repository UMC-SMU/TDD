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





