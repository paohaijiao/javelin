/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package condition;

import com.github.paohaijiao.condition.*;
import com.github.paohaijiao.condition.impl.AsyncConditionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName condition
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/2/11
 */
@DisplayName("条件服务测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.CONCURRENT)
public class ConditionServiceTest {

    private ConditionService<String, String> stringService;
    private ConditionService<Integer, Boolean> integerService;
    private ConditionService<User, String> userService;

    @BeforeEach
    void setUp() {
        stringService = ConditionServiceFactory.createService(ConditionServiceFactory.ServiceType.SIMPLE);
        integerService = ConditionServiceFactory.createService(ConditionServiceFactory.ServiceType.SIMPLE);
        userService = new SimpleConditionService<>();
    }

    @Test
    @Order(1)
    @DisplayName("测试Condition和Predicate方法区分 - 编译期重载冲突验证")
    void testMethodOverloadResolution() {

        Condition<String> nonEmptyCondition = s -> s != null && !s.isEmpty();
        boolean result1 = stringService.testWith("hello", nonEmptyCondition);
        assertTrue(result1);
        // 使用Predicate
        Predicate<String> nonEmptyPredicate = s -> s != null && !s.isEmpty();
        boolean result2 = stringService.testWithPredicate("world", nonEmptyPredicate);
        assertTrue(result2);
        assertFalse(stringService.testWith("", nonEmptyCondition));
        assertFalse(stringService.testWithPredicate("", nonEmptyPredicate));
    }

    @Test
    @Order(2)
    @DisplayName("测试基本条件判断 - Condition")
    void testTestWithCondition() {
        Condition<Integer> isPositive = i -> i != null && i > 0;
        Condition<Integer> isEven = i -> i != null && i % 2 == 0;

        assertTrue(integerService.testWith(5, isPositive));
        assertFalse(integerService.testWith(-1, isPositive));
        assertTrue(integerService.testWith(4, isEven));
        assertFalse(integerService.testWith(3, isEven));
        assertFalse(integerService.testWith(null, isPositive));
    }

    @Test
    @Order(3)
    @DisplayName("测试基本条件判断 - Predicate")
    void testTestWithPredicate() {
        Predicate<Integer> isPositive = i -> i != null && i > 0;
        Predicate<Integer> isEven = i -> i != null && i % 2 == 0;

        assertTrue(integerService.testWithPredicate(5, isPositive));
        assertFalse(integerService.testWithPredicate(-1, isPositive));
        assertTrue(integerService.testWithPredicate(4, isEven));
        assertFalse(integerService.testWithPredicate(3, isEven));
        assertFalse(integerService.testWithPredicate(null, isPositive));
    }
    @Test
    @Order(4)
    @DisplayName("测试条件映射 - Condition")
    void testMapIfWithCondition() {
        Condition<String> isNotEmpty = s -> s != null && !s.isEmpty();
        Function<String, String> toUpperCase = String::toUpperCase;

        Optional<String> result1 = stringService.mapIf("hello", isNotEmpty, toUpperCase);
        assertTrue(result1.isPresent());
        assertEquals("HELLO", result1.get());

        Optional<String> result2 = stringService.mapIf("", isNotEmpty, toUpperCase);
        assertTrue(result2.isPresent());

        Optional<String> result3 = stringService.mapIf(null, isNotEmpty, toUpperCase);
        assertTrue(result3.isPresent());
    }

    @Test
    @Order(5)
    @DisplayName("测试条件映射 - Predicate")
    void testMapIfWithPredicate() {
        Predicate<String> isNotEmpty = s -> s != null && !s.isEmpty();
        Function<String, String> toLowerCase = String::toLowerCase;

        Optional<String> result1 = stringService.mapIfWithPredicate("HELLO", isNotEmpty, toLowerCase);
        assertTrue(result1.isPresent());
        assertEquals("hello", result1.get());

        Optional<String> result2 = stringService.mapIfWithPredicate("", isNotEmpty, toLowerCase);
        assertTrue(result2.isPresent());
    }


    @Test
    @Order(6)
    @DisplayName("测试条件消费 - Condition")
    void testAcceptIfWithCondition() {
        Condition<String> isLongString = s -> s != null && s.length() > 5;
        AtomicBoolean consumed = new AtomicBoolean(false);

        stringService.acceptIf("hello world", isLongString, s -> consumed.set(true));
        assertTrue(consumed.get());

        consumed.set(false);
        stringService.acceptIf("hi", isLongString, s -> consumed.set(true));
        assertFalse(consumed.get());
    }

    @Test
    @Order(7)
    @DisplayName("测试条件消费 - Predicate")
    void testAcceptIfWithPredicate() {
        Predicate<String> isLongString = s -> s != null && s.length() > 5;
        StringBuilder sb = new StringBuilder();

        stringService.acceptIfWithPredicate("hello world", isLongString, sb::append);
        assertEquals("hello world", sb.toString());

        stringService.acceptIfWithPredicate("hi", isLongString, sb::append);
        assertEquals("hello world", sb.toString()); // 长度不变
    }

    @Test
    @Order(8)
    @DisplayName("测试条件获取 - Condition")
    void testGetIfWithCondition() {
        Condition<Integer> isPositive = i -> i != null && i > 0;
        Supplier<Boolean> trueSupplier = () -> true;
        assertTrue(integerService.getIf(5, isPositive, trueSupplier, false));
        assertFalse(integerService.getIf(-1, isPositive, trueSupplier, false));
        assertFalse(integerService.getIf(null, isPositive, trueSupplier, false));
    }



    @Test
    @Order(10)
    @DisplayName("测试多条件组合 - 全满足(Condition)")
    void testTestAllWithCondition() {
        Condition<Integer> isPositive = i -> i != null && i > 0;
        Condition<Integer> isEven = i -> i != null && i % 2 == 0;
        Condition<Integer> isLessThan10 = i -> i != null && i < 10;

        assertTrue(integerService.testAll(4, isPositive, isEven, isLessThan10));
        assertFalse(integerService.testAll(6, isPositive, isEven, isLessThan10)); // 6 < 10, 满足
        assertFalse(integerService.testAll(12, isPositive, isEven, isLessThan10));
        assertFalse(integerService.testAll(3, isPositive, isEven, isLessThan10));
    }

    @Test
    @Order(11)
    @DisplayName("测试多条件组合 - 全满足(Predicate)")
    void testTestAllWithPredicate() {
        Predicate<String> nonNull = Objects::nonNull;
        Predicate<String> nonEmpty = s -> !s.isEmpty();
        Predicate<String> lengthGt3 = s -> s.length() > 3;

        assertTrue(stringService.testAllWithPredicate("hello", nonNull, nonEmpty, lengthGt3));
        assertFalse(stringService.testAllWithPredicate("hi", nonNull, nonEmpty, lengthGt3));
        assertFalse(stringService.testAllWithPredicate("", nonNull, nonEmpty, lengthGt3));
        assertFalse(stringService.testAllWithPredicate(null, nonNull, nonEmpty, lengthGt3));
    }

    @Test
    @Order(12)
    @DisplayName("测试多条件组合 - 任一满足(Condition)")
    void testTestAnyWithCondition() {
        Condition<Integer> isNegative = i -> i != null && i < 0;
        Condition<Integer> isZero = i -> i != null && i == 0;
        Condition<Integer> isPositive = i -> i != null && i > 0;

        assertTrue(integerService.testAny(-5, isNegative, isZero, isPositive));
        assertTrue(integerService.testAny(0, isNegative, isZero, isPositive));
        assertTrue(integerService.testAny(5, isNegative, isZero, isPositive));
        assertFalse(integerService.testAny(null, isNegative, isZero, isPositive));
    }

    @Test
    @Order(13)
    @DisplayName("测试多条件组合 - 任一满足(Predicate)")
    void testTestAnyWithPredicate() {
        Predicate<String> containsA = s -> s.contains("a");
        Predicate<String> containsB = s -> s.contains("b");
        Predicate<String> containsC = s -> s.contains("c");

        assertTrue(stringService.testAnyWithPredicate("apple", containsA, containsB, containsC));
        assertTrue(stringService.testAnyWithPredicate("banana", containsA, containsB, containsC));
        assertTrue(stringService.testAnyWithPredicate("cat", containsA, containsB, containsC));
        assertFalse(stringService.testAnyWithPredicate("dog", containsA, containsB, containsC));
    }
    @Test
    @Order(14)
    @DisplayName("测试验证器模式 - 验证通过")
    void testValidatorValid() {
        User user = new User("john.doe", "john@example.com", 25);
        Validator<User> validator = userService.validator(user)
                .validateWith(Condition.nonNull(), "用户不能为null")
                .validateWith(Condition.of(u -> u.getUsername() != null && !u.getUsername().isEmpty()), "用户名不能为空")
                .validateWith(Condition.of(u -> u.getEmail() != null && u.getEmail().contains("@")), "邮箱格式不正确")
                .validateWith(Condition.of(u -> u.getAge() >= 18), "年龄必须大于等于18岁");

        assertTrue(validator.isValid());
        assertTrue(validator.getErrors().isEmpty());
        assertEquals(user, validator.getValidatedValue());
    }

    @Test
    @Order(15)
    @DisplayName("测试验证器模式 - 使用Predicate")
    void testValidatorWithPredicate() {
        User user = new User("john.doe", "john@example.com", 25);
        Validator<User> validator = userService.validator(user)
                .validateWithPredicate(Objects::nonNull, "用户不能为null")
                .validateWithPredicate(u -> u.getUsername() != null && !u.getUsername().isEmpty(), "用户名不能为空")
                .validateWithPredicate(u -> u.getEmail() != null && u.getEmail().contains("@"), "邮箱格式不正确")
                .validateWithPredicate(u -> u.getAge() >= 18, "年龄必须大于等于18岁");

        assertTrue(validator.isValid());
        assertTrue(validator.getErrors().isEmpty());
        assertEquals(user, validator.getValidatedValue());
    }

    @Test
    @Order(16)
    @DisplayName("测试验证器模式 - 混合使用Condition和Predicate")
    void testValidatorMixed() {
        User user = new User("", "invalid-email", 16);
        Validator<User> validator = userService.validator(user)
                .validateWith(Condition.nonNull(), "用户不能为null")
                .validateWithPredicate(u -> u.getUsername() != null && !u.getUsername().isEmpty(), "用户名不能为空")
                .validateWith(Condition.of(u -> u.getEmail() != null && u.getEmail().contains("@")), "邮箱格式不正确")
                .validateWithPredicate(u -> u.getAge() >= 18, "年龄必须大于等于18岁");
        assertFalse(validator.isValid());
        assertEquals(3, validator.getErrors().size());
        assertTrue(validator.getErrors().contains("用户名不能为空"));
        assertTrue(validator.getErrors().contains("邮箱格式不正确"));
        assertTrue(validator.getErrors().contains("年龄必须大于等于18岁"));
        assertThrows(ValidationException.class, validator::getValidatedValue);
    }

    @Test
    @Order(17)
    @DisplayName("测试验证器模式 - 静态条件验证")
    void testValidatorWithStaticConditions() {
        User user = new User(null, null, 0);
        Validator<User> validator = userService.validator(user)
                .validateWith(Condition.isNull(), "用户名应为null")  // 使用Condition静态方法
                .validateWithPredicate(u -> u.getEmail() == null, "邮箱应为null")
                .validateWith(Condition.equalTo(0).compose(User::getAge), "年龄应为0");
        assertTrue(validator.isValid());
    }

    @Test
    @Order(18)
    @DisplayName("测试验证器模式 - 链式验证短路")
    void testValidatorChaining() {
        User user = new User("john", null, 25);
        Validator<User> validator = userService.validator(user)
                .validateWith(Condition.nonNull(), "用户不能为null")
                .validateWithPredicate(u -> u.getUsername().length() > 3, "用户名长度必须大于3")
                .validateWith(Condition.of(u -> u.getEmail() != null), "邮箱不能为空")  // 这个会失败
                .validateWithPredicate(u -> u.getAge() >= 18, "年龄必须大于等于18岁");
        assertFalse(validator.isValid());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().contains("邮箱不能为空"));
    }

    @Test
    @Order(19)
    @DisplayName("测试验证器模式 - 空值验证")
    void testValidatorWithNull() {
        Validator<User> validator = userService.validator(null)
                .validateWith(Condition.nonNull(), "用户不能为null");
        assertFalse(validator.isValid());
        assertEquals(1, validator.getErrors().size());
        assertThrows(ValidationException.class, validator::getValidatedValue);
    }

    @Test
    @Order(20)
    @DisplayName("测试验证器模式 - 复杂组合条件")
    void testValidatorWithComplexCondition() {
        User user = new User("admin", "admin@example.com", 30);
        Condition<User> validUserCondition = ConditionBuilder.FluentBuilder.<User>create()
                .addNotNull()
                .addWithConversion(User::getAge, age -> age >= 18)
                .addWithConversionFromPredicate(User::getEmail,
                        email -> email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                .addConverted(User::getUsername)
                .toPredicate(username -> username != null && username.length() > 3)
                .buildAnd();

        User validUser = new User("john123", "john@example.com", 25);
        assertTrue(validUserCondition.test(validUser));

        User invalidAge = new User("john123", "john@example.com", 16);
        assertFalse(validUserCondition.test(invalidAge));

        User invalidEmail = new User("john123", "invalid-email", 25);
        assertFalse(validUserCondition.test(invalidEmail));

        User shortName = new User("jo", "john@example.com", 25);
        assertFalse(validUserCondition.test(shortName));
    }

    @Test
    @DisplayName("测试组合条件构建 - Condition版本")
    void testCombinatorsWithCondition() {
        Condition<String> nonNull = Condition.nonNull();
        Condition<String> lengthGt3 = s -> s.length() > 3;
        Condition<String> startsWithH = s -> s.startsWith("h");
        Condition<String> allCondition = ConditionBuilder.allOf(nonNull, lengthGt3, startsWithH);
        assertTrue(allCondition.test("hello"));
        assertFalse(allCondition.test("hi"));
        assertFalse(allCondition.test(null));
        Condition<String> anyCondition = ConditionBuilder.anyOf(nonNull, lengthGt3, startsWithH);
        assertTrue(anyCondition.test("hello"));
        assertTrue(anyCondition.test("h"));
        assertFalse(anyCondition.test(null));
        Condition<String> noneCondition = ConditionBuilder.noneOf(lengthGt3);
        assertTrue(noneCondition.test("hi"));
        assertFalse(noneCondition.test("hello"));
    }

    @Test
    @DisplayName("测试组合条件构建 - Predicate版本")
    void testCombinatorsWithPredicate() {
        Predicate<String> nonNull = Objects::nonNull;
        Predicate<String> lengthGt3 = s -> s.length() > 3;
        Predicate<String> startsWithH = s -> s.startsWith("h");
        Condition<String> allCondition = ConditionBuilder.allOfPredicates(nonNull, lengthGt3, startsWithH);
        assertTrue(allCondition.test("hello"));
        assertFalse(allCondition.test("hi"));
        Condition<String> anyCondition = ConditionBuilder.anyOfPredicates(nonNull, lengthGt3, startsWithH);
        assertTrue(anyCondition.test("hello"));
        assertTrue(anyCondition.test("h"));
        Condition<String> noneCondition = ConditionBuilder.noneOfPredicate(lengthGt3);
        assertTrue(noneCondition.test("hi"));
        assertFalse(noneCondition.test("hello"));
    }

    @Test
    @DisplayName("测试类型转换条件 - Condition版本")
    void testWithConversionCondition() {
        Function<String, Integer> toLength = String::length;
        Condition<Integer> isEven = i -> i % 2 == 0;
        Condition<String> evenLength = ConditionBuilder.withConversion(toLength, isEven);
        assertTrue(evenLength.test("abcd"));  // 长度4，偶数
        assertFalse(evenLength.test("abc"));  // 长度3，奇数
    }

    @Test
    @DisplayName("测试类型转换条件 - Predicate版本")
    void testWithConversionFromPredicate() {
        Function<String, Integer> toLength = String::length;
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Condition<String> evenLength = ConditionBuilder.withConversionFromPredicate(toLength, isEven);
        assertTrue(evenLength.test("abcd"));
        assertFalse(evenLength.test("abc"));
    }



    @Test
    @DisplayName("测试FluentBuilder - 添加Condition条件")
    void testFluentBuilderAddCondition() {
        Condition<String> condition = ConditionBuilder.FluentBuilder.<String>create()
                .addCondition(Condition.nonNull())
                .addCondition(s -> s.length() > 3)
                .addCondition(s -> s.startsWith("h"))
                .buildAnd();
        assertTrue(condition.test("hello"));
        assertFalse(condition.test("hi"));
        assertFalse(condition.test(null));
    }

    @Test
    @DisplayName("测试FluentBuilder - 添加Predicate条件")
    void testFluentBuilderAddPredicate() {
        Condition<String> condition = ConditionBuilder.FluentBuilder.<String>create()
                .addPredicate(Objects::nonNull)
                .addPredicate(s -> s.length() > 3)
                .addPredicate(s -> s.startsWith("h"))
                .buildAnd();
        assertTrue(condition.test("hello"));
        assertFalse(condition.test("hi"));
    }

    @Test
    @DisplayName("测试FluentBuilder - 便捷条件添加")
    void testFluentBuilderConvenienceMethods() {
        Condition<String> condition = ConditionBuilder.FluentBuilder.<String>create()
                .addNotNull()
                .addIsNull()
                .addEqualTo("test")
                .buildAnd();
        assertFalse(condition.test("test"));
        assertFalse(condition.test(null));
    }

    @Test
    @DisplayName("测试FluentBuilder - 带类型转换的条件添加")
    void testFluentBuilderWithConversion() {
        Condition<User> condition1 = ConditionBuilder.FluentBuilder.<User>create()
                .addNotNull()
                .addWithConversion(User::getAge, age -> age >= 18)
                .addWithConversionFromPredicate(User::getEmail, email -> email.contains("@"))
                .buildAnd();
        User adult = new User("john", "john@test.com", 25);
        assertTrue(condition1.test(adult));
        User minor = new User("jim", "jim@test.com", 16);
        assertFalse(condition1.test(minor));
        Condition<User> condition2 = ConditionBuilder.FluentBuilder.<User>create()
                .addNotNull()
                .addConverted(User::getAge).to(age -> age >= 18)
                .addConverted(User::getEmail).toPredicate(email -> email.contains("@"))
                .buildAnd();

        assertTrue(condition2.test(adult));
        assertFalse(condition2.test(minor));
    }

    @Test
    @DisplayName("测试FluentBuilder - 不同构建模式")
    void testFluentBuilderBuildModes() {
        ConditionBuilder.FluentBuilder<String> builder = ConditionBuilder.FluentBuilder.<String>create()
                .addCondition(s -> s.length() > 3)
                .addCondition(s -> s.contains("a"))
                .addCondition(s -> s.startsWith("h"));
        Condition<String> andCondition = builder.buildAnd();
        assertTrue(andCondition.test("hello"));
        assertFalse(andCondition.test("world"));
        assertFalse(andCondition.test("hi"));
        Condition<String> orCondition = builder.buildOr();
        assertTrue(orCondition.test("hello"));
        assertTrue(orCondition.test("world"));  // 包含'a'
        assertFalse(orCondition.test("hi"));    // 长度<=3
        Condition<String> noneCondition = builder.buildNone();
        assertFalse(noneCondition.test("hello"));
        assertFalse(noneCondition.test("world"));
        assertTrue(noneCondition.test("hi"));
    }

    @Test
    @DisplayName("测试静态工厂方法")
    void testStaticFactoryMethods() {
        Condition<String> condition1 = ConditionBuilder.<String>and()
                .addNotNull()
                .addPredicate(s -> s.length() > 3)
                .buildAnd();
        assertTrue(condition1.test("test"));
        assertFalse(condition1.test(""));
        Condition<String> condition2 = ConditionBuilder.of(Condition.nonNull());
        assertTrue(condition2.test("test"));
        assertFalse(condition2.test(null));
        Condition<String> condition3 = ConditionBuilder.ofPredicate(Objects::nonNull);
        assertTrue(condition3.test("test"));
        assertFalse(condition3.test(null));
    }

    @Test
    @DisplayName("测试复杂业务场景")
    void testComplexScenario() {
        // 构建用户验证条件：年龄>=18，邮箱有效，用户名非空且长度>3
        Condition<User> validUserCondition = ConditionBuilder.FluentBuilder.<User>create()
                .addNotNull()
                .addWithConversion(User::getAge, age -> age >= 18)
                .addWithConversionFromPredicate(User::getEmail,
                        email -> email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                .addConverted(User::getUsername)
                .toPredicate(username -> username != null && username.length() > 3)
                .buildAnd();

        User validUser = new User("john123", "john@example.com", 25);
        assertTrue(validUserCondition.test(validUser));

        User invalidAge = new User("john123", "john@example.com", 16);
        assertFalse(validUserCondition.test(invalidAge));

        User invalidEmail = new User("john123", "invalid-email", 25);
        assertFalse(validUserCondition.test(invalidEmail));

        User shortName = new User("jo", "john@example.com", 25);
        assertFalse(validUserCondition.test(shortName));
    }
    @Test
    @Order(18)
    @DisplayName("测试条件链 - then执行动作")
    void testConditionChainThen() {
        AtomicInteger counter = new AtomicInteger(0);

        stringService.chain("test")
                .when(Condition.nonNull())
                .then(v -> counter.incrementAndGet())
                .then(v -> counter.incrementAndGet());
        assertEquals(2, counter.get());
        stringService.chain((String) null)
                .when(Condition.nonNull())
                .then(v -> counter.incrementAndGet());

        assertEquals(2, counter.get()); // 没有增加
    }
    @Test
    @Order(19)
    @DisplayName("测试Condition组合功能")
    void testConditionCombination() {
        Condition<Integer> isPositive = i -> i > 0;
        Condition<Integer> isEven = i -> i % 2 == 0;
        Condition<Integer> isLessThan10 = i -> i < 10;
        Condition<Integer> positiveAndEven = isPositive.and(isEven);
        assertTrue(positiveAndEven.test(4));
        assertFalse(positiveAndEven.test(3));
        assertFalse(positiveAndEven.test(-2));
    }

    @Test
    @Order(20)
    @DisplayName("测试ConditionBuilder")
    void testConditionBuilder() {
        // 测试 allOf
        Condition<String> allOf = ConditionBuilder.allOf(
                Condition.nonNull(),
                s -> s.length() > 3,
                s -> s.startsWith("a")
        );
        assertTrue(allOf.test("apple"));
        assertFalse(allOf.test("banana"));
        assertFalse(allOf.test("a"));

        // 测试 anyOf
        Condition<String> anyOf = ConditionBuilder.anyOf(
                s -> s.contains("a"),
                s -> s.contains("b"),
                s -> s.contains("c")
        );
        assertTrue(anyOf.test("apple"));
        assertTrue(anyOf.test("banana"));
        assertFalse(anyOf.test("dog"));
    }
    @Nested
    @DisplayName("异步服务测试")
    class AsyncServiceTest {

        private AsyncConditionService<String, String> asyncStringService;
        private ExecutorService customExecutor;

        @BeforeEach
        void setUp() {
            asyncStringService = new AsyncConditionService<>();
            customExecutor = Executors.newFixedThreadPool(2);
        }

        @AfterEach
        void tearDown() {
            asyncStringService.shutdown();
            customExecutor.shutdown();
        }

        @Test
        @DisplayName("测试异步基本条件判断")
        void testAsyncTest() throws ExecutionException, InterruptedException {
            Condition<String> isNotEmpty = s -> s != null && !s.isEmpty();

            CompletableFuture<Boolean> future1 = asyncStringService.testWithAsync("hello", isNotEmpty);
            assertTrue(future1.get());

            CompletableFuture<Boolean> future2 = asyncStringService.testWithPredicateAsync("", s -> !s.isEmpty());
            assertFalse(future2.get());
        }

        @Test
        @DisplayName("测试异步条件映射")
        void testAsyncMapIf() throws ExecutionException, InterruptedException {
            CompletableFuture<Optional<String>> future = asyncStringService.mapIfAsync(
                    "hello",
                    Condition.nonNull(),
                    String::toUpperCase
            );

            Optional<String> result = future.get();
            assertTrue(result.isPresent());
            assertEquals("HELLO", result.get());
        }

        @Test
        @DisplayName("测试异步条件消费")
        void testAsyncAcceptIf() throws ExecutionException, InterruptedException {
            AtomicBoolean executed = new AtomicBoolean(false);

            CompletableFuture<Void> future = asyncStringService.acceptIfAsync(
                    "test",
                    Condition.alwaysTrue(),
                    s -> executed.set(true)
            );

            future.get();
            assertTrue(executed.get());
        }

        @Test
        @DisplayName("测试自定义线程池")
        void testCustomExecutor() {
            AsyncConditionService<String, String> customService =
                    new AsyncConditionService<>(customExecutor);

            CompletableFuture<Boolean> future = customService.testWithAsync(
                    "test",
                    Condition.alwaysTrue()
            );

            assertTrue(future.join());
            customService.shutdown();
        }

        @Test
        @DisplayName("测试异步并发执行")
        void testAsyncConcurrent() throws InterruptedException, ExecutionException {
            CountDownLatch latch = new CountDownLatch(3);
            AtomicInteger counter = new AtomicInteger(0);

            CompletableFuture<Void> future1 = asyncStringService.acceptIfAsync(
                    "task1", Condition.alwaysTrue(), s -> {
                        counter.incrementAndGet();
                        latch.countDown();
                    });

            CompletableFuture<Void> future2 = asyncStringService.acceptIfAsync(
                    "task2", Condition.alwaysTrue(), s -> {
                        counter.incrementAndGet();
                        latch.countDown();
                    });

            CompletableFuture<Void> future3 = asyncStringService.acceptIfAsync(
                    "task3", Condition.alwaysTrue(), s -> {
                        counter.incrementAndGet();
                        latch.countDown();
                    });

            CompletableFuture.allOf(future1, future2, future3).get();
            assertEquals(3, counter.get());
        }
    }
    @Nested
    @DisplayName("异常情况测试")
    class ExceptionTest {

        @Test
        @DisplayName("测试null值处理")
        void testNullHandling() {
            Condition<String> nonNull = Condition.nonNull();

            assertFalse(stringService.testWith(null, nonNull));
            assertTrue(stringService.testWithPredicate("", s -> s.isEmpty()));

            Optional<String> result = stringService.mapIf(null, nonNull, String::toUpperCase);
            assertTrue(result.isPresent());
        }

    }

    /**
     * 测试用用户实体
     */
    static class User {
        private final String username;
        private final String email;
        private final int age;

        User(String username, String email, int age) {
            this.username = username;
            this.email = email;
            this.age = age;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public int getAge() { return age; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return age == user.age &&
                    Objects.equals(username, user.username) &&
                    Objects.equals(email, user.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username, email, age);
        }

        @Override
        public String toString() {
            return "User{username='" + username + "', email='" + email + "', age=" + age + '}';
        }
    }
}
