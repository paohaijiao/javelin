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
package com.github.paohaijiao.model;

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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;

/**
 * 复杂用户模型 - 包含多种数据类型
 * 用于测试 JQuickRow 转换器的各种类型支持
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JBenchMarkUserModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 年龄 */
    private Integer age;

    /** 是否启用 */
    private Boolean enabled;

    /** 是否VIP */
    private boolean vip;

    /** 评分 (0-100) */
    private double score;

    /** 余额 */
    private BigDecimal balance;

    /** 等级 */
    private int level;


    /** 出生日期 */
    private Date birthDate;

    /** 注册时间 */
    private LocalDateTime registerTime;

    /** 会员到期日 */
    private LocalDate membershipExpiryDate;

    /** 最后登录时间 */
    private java.sql.Timestamp lastLoginTime;


    /** 标签列表 */
    private List<String> tags;

    /** 兴趣列表 */
    private List<String> hobbies;

    /** 技能列表 (技能名 -> 熟练度) */
    private List<Skill> skills;

    /** 订单ID列表 */
    private Set<Long> orderIds;

    /** 好友ID列表 */
    private List<Long> friendIds;

    /** 设备信息 */
    private Map<String, String> deviceInfo;

    /** 扩展属性 */
    private Map<String, Object> extraProperties;

    /** 用户状态 */
    private UserStatus status;

    /** 性别 */
    private Gender gender;

    /** 用户类型 */
    private UserType userType;


    /** 个人资料 */
    private Profile profile;

    /** 地址信息 */
    private Address address;

    /** 公司信息 */
    private Company company;

    /** 账户信息 */
    private Account account;

    /** 头像URL列表 */
    private String[] avatarUrls;

    /** 权限编码 */
    private int[] permissionCodes;

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        /** 待激活 */
        PENDING("待激活"),
        /** 正常 */
        ACTIVE("正常"),
        /** 已冻结 */
        FROZEN("已冻结"),
        /** 已注销 */
        DELETED("已注销");

        private final String description;

        UserStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 性别枚举
     */
    public enum Gender {
        MALE("男"),
        FEMALE("女"),
        UNKNOWN("未知");

        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 用户类型枚举
     */
    public enum UserType {
        /** 普通用户 */
        NORMAL(0, "普通用户"),
        /** VIP用户 */
        VIP(1, "VIP用户"),
        /** 企业用户 */
        ENTERPRISE(2, "企业用户"),
        /** 管理员 */
        ADMIN(9, "管理员");

        private final int code;
        private final String description;

        UserType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static UserType fromCode(int code) {
            for (UserType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return NORMAL;
        }
    }
    /**
     * 技能信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Skill implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 技能名称 */
        private String name;

        /** 熟练度 (1-10) */
        private int proficiency;

        /** 技能等级 */
        private SkillLevel level;

        /** 是否认证 */
        private boolean certified;

        /** 获得时间 */
        private Date acquiredDate;

        @Override
        public String toString() {
            return name + " (Lv." + level + ", " + proficiency + "/10)";
        }
    }

    /**
     * 技能等级枚举
     */
    public enum SkillLevel {
        BEGINNER("初级", 1),
        INTERMEDIATE("中级", 2),
        ADVANCED("高级", 3),
        EXPERT("专家", 4),
        MASTER("大师", 5);

        private final String displayName;
        private final int value;

        SkillLevel(String displayName, int value) {
            this.displayName = displayName;
            this.value = value;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 个人资料
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 真实姓名 */
        private String realName;

        /** 身份证号 */
        private String idNumber;

        /** 手机号 */
        private String phone;

        /** 邮箱 */
        private String email;

        /** QQ号 */
        private String qq;

        /** 微信号 */
        private String wechat;

        /** 个人简介 */
        private String bio;

        /** 头像URL */
        private String avatarUrl;

        /** 背景图URL */
        private String coverUrl;

        /** 个人网站 */
        private String website;
    }

    /**
     * 地址信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 省份 */
        private String province;

        /** 城市 */
        private String city;

        /** 区/县 */
        private String district;

        /** 详细地址 */
        private String detail;

        /** 邮政编码 */
        private String zipCode;

        /** 经度 */
        private Double longitude;

        /** 纬度 */
        private Double latitude;

        /** 地址类型 (家庭/公司/其他) */
        private AddressType type;

        public String getFullAddress() {
            return province + city + district + detail;
        }
    }

    /**
     * 地址类型枚举
     */
    public enum AddressType {
        HOME("家庭"),
        COMPANY("公司"),
        OTHER("其他");

        private final String displayName;

        AddressType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 公司信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Company implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 公司名称 */
        private String name;

        /** 公司简称 */
        private String shortName;

        /** 统一社会信用代码 */
        private String creditCode;

        /** 行业 */
        private String industry;

        /** 公司规模 */
        private Integer employeeCount;

        /** 成立日期 */
        private Date establishedDate;

        /** 公司地址 */
        private Address address;

        /** 公司网站 */
        private String website;

        /** 公司电话 */
        private String phone;

        /** 公司邮箱 */
        private String email;

        private boolean open;

        public Company copy() {
            return new Company(
                    this.name,
                    this.shortName,
                    this.creditCode,
                    this.industry,
                    this.employeeCount,
                    this.establishedDate,
                    this.address,
                    this.website,
                    this.phone,
                    this.email,
                    this.open);
        }
    }

    /**
     * 账户信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Account implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 账户ID */
        private Long accountId;

        /** 账户余额 */
        private BigDecimal balance;

        /** 冻结金额 */
        private BigDecimal frozenAmount;

        /** 总充值 */
        private BigDecimal totalRecharge;

        /** 总消费 */
        private BigDecimal totalConsume;

        /** 积分 */
        private Long points;

        /** 等级积分 */
        private Long levelPoints;

        /** 账户状态 */
        private AccountStatus status;

        /** 创建时间 */
        private Date createTime;

        /** 更新时间 */
        private Date updateTime;
    }

    /**
     * 账户状态枚举
     */
    public enum AccountStatus {
        NORMAL("正常"),
        FROZEN("冻结"),
        CLOSED("已关闭");

        private final String displayName;

        AccountStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }



    /**
     * 创建示例用户
     */
    public static JBenchMarkUserModel createSampleUser() {
        // 创建地址
        Address address = Address.builder()
                .province("广东省")
                .city("深圳市")
                .district("南山区")
                .detail("科技园南区XX大厦")
                .zipCode("518000")
                .longitude(113.9445)
                .latitude(22.5431)
                .type(AddressType.COMPANY)
                .build();

        // 创建公司
        Company company = Company.builder()
                .name("深圳科技有限公司")
                .shortName("深圳科技")
                .creditCode("91440300MA5XXXXXXX")
                .industry("互联网")
                .employeeCount(500)
                .establishedDate(new Date())
                .address(address)
                .website("https://example.com")
                .phone("0755-88888888")
                .email("contact@example.com")
                .build();

        // 创建个人资料
        Profile profile = Profile.builder()
                .realName("张三")
                .idNumber("440301199001011234")
                .phone("13800138000")
                .email("zhangsan@example.com")
                .qq("12345678")
                .wechat("zhangsan_wechat")
                .bio("热爱编程，喜欢技术分享")
                .avatarUrl("https://example.com/avatar/zhangsan.jpg")
                .coverUrl("https://example.com/cover/zhangsan.jpg")
                .website("https://blog.example.com")
                .build();

        // 创建账户
        Account account = Account.builder()
                .accountId(10001L)
                .balance(new BigDecimal("8888.88"))
                .frozenAmount(new BigDecimal("0.00"))
                .totalRecharge(new BigDecimal("10000.00"))
                .totalConsume(new BigDecimal("1111.12"))
                .points(8888L)
                .levelPoints(10000L)
                .status(AccountStatus.NORMAL)
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        // 创建技能列表
        List<Skill> skills = Arrays.asList(
                Skill.builder()
                        .name("Java")
                        .proficiency(9)
                        .level(SkillLevel.EXPERT)
                        .certified(true)
                        .acquiredDate(new Date())
                        .build(),
                Skill.builder()
                        .name("Python")
                        .proficiency(7)
                        .level(SkillLevel.ADVANCED)
                        .certified(true)
                        .acquiredDate(new Date())
                        .build(),
                Skill.builder()
                        .name("Go")
                        .proficiency(5)
                        .level(SkillLevel.INTERMEDIATE)
                        .certified(false)
                        .acquiredDate(new Date())
                        .build()
        );

        // 构建设备信息
        Map<String, String> deviceInfo = new HashMap<>();
        deviceInfo.put("os", "Windows 11");
        deviceInfo.put("browser", "Chrome 120");
        deviceInfo.put("screen", "1920x1080");

        // 构建扩展属性
        Map<String, Object> extraProperties = new HashMap<>();
        extraProperties.put("favoriteColor", "blue");
        extraProperties.put("preferredLanguage", "zh-CN");
        extraProperties.put("notificationEnabled", true);
        extraProperties.put("theme", "dark");

        return JBenchMarkUserModel.builder()
                .id(10086L)
                .username("zhangsan")
                .nickname("三哥")
                .age(25)
                .enabled(true)
                .vip(true)
                .score(95.5)
                .balance(new BigDecimal("9999.99"))
                .level(5)
                .birthDate(new Date())
                .registerTime(LocalDateTime.now())
                .membershipExpiryDate(LocalDate.now().plusYears(1))
                .lastLoginTime(new java.sql.Timestamp(System.currentTimeMillis()))
                .tags(Arrays.asList("技术达人", "开源爱好者", "博客作者"))
                .hobbies(Arrays.asList("编程", "阅读", "健身", "旅行"))
                .skills(skills)
                .orderIds(new HashSet<>(Arrays.asList(1001L, 1002L, 1003L)))
                .friendIds(Arrays.asList(10001L, 10002L, 10003L, 10004L))
                .deviceInfo(deviceInfo)
                .extraProperties(extraProperties)
                .status(UserStatus.ACTIVE)
                .gender(Gender.MALE)
                .userType(UserType.ENTERPRISE)
                .profile(profile)
                .address(address)
                .company(company.copy())
                    .account(account)
                .avatarUrls(new String[]{
                        "https://example.com/avatar/1.jpg",
                        "https://example.com/avatar/2.jpg",
                        "https://example.com/avatar/3.jpg"
                })
                .permissionCodes(new int[]{1, 2, 4, 8, 16, 32})
                .build();
    }

    /**
     * 创建包含多个用户的示例列表
     */
    public static List<JBenchMarkUserModel> createSampleUsers(int count) {
        List<JBenchMarkUserModel> users = new ArrayList<>();
        String[] names = {"张三", "李四", "王五", "赵六", "孙七", "周八", "吴九", "郑十"};
        String[] nicknames = {"三哥", "四爷", "五哥", "六爷", "七哥", "八爷", "九哥", "十爷"};
        UserStatus[] statuses = UserStatus.values();
        Gender[] genders = Gender.values();
        UserType[] userTypes = UserType.values();
        Company company = new Company();
        company.setName("A");
        company.setShortName("A-short");
        company.setIndustry("IT");
        company.setWebsite("https://a.example.com");

        for (int i = 0; i < count; i++) {
            company.setOpen(i % 2 == 0);
            int idx = i % names.length;
            JBenchMarkUserModel user = JBenchMarkUserModel.builder()
                    .id(10000L + i)
                    .username(names[idx] + i)
                    .nickname(nicknames[idx] + i)
                    .age(20 + (i % 30))
                    .enabled(i % 3 != 1)
                    .vip(i % 2 == 0)
                    .score(50 + (i % 50) + (i * 0.5))
                    .balance(new BigDecimal("1000" + (i % 9) + "." + (i % 10) + (i % 10)))
                    .level(i % 10)
                    .birthDate(new Date(System.currentTimeMillis() - (long) i * 365 * 24 * 60 * 60 * 1000))
                    .registerTime(LocalDateTime.now().minusDays(i))
                    .membershipExpiryDate(LocalDate.now().plusDays(365 - i))
                    .lastLoginTime(new java.sql.Timestamp(System.currentTimeMillis() - (long) i * 24 * 60 * 60 * 1000))
                    .tags(Arrays.asList("tag" + i + "-1", "tag" + i + "-2", "tag" + i + "-3"))
                    .hobbies(Arrays.asList("hobby" + i + "-1", "hobby" + i + "-2"))
                    .status(statuses[i % statuses.length])
                    .gender(genders[i % genders.length])
                    .userType(userTypes[i % userTypes.length])
                    .company(company.copy())
                    .build();
            users.add(user);
        }
        return users;
    }

    @Override
    public String toString() {
        return "JUserModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", age=" + age +
                ", enabled=" + enabled +
                ", vip=" + vip +
                ", score=" + score +
                ", balance=" + balance +
                ", level=" + level +
                ", status=" + status +
                ", gender=" + gender +
                ", userType=" + userType +
                ", tags=" + tags +
                ", skills=" + skills +
                '}';
    }
}