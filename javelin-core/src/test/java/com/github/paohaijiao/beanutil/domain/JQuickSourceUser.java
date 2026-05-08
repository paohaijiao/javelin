package com.github.paohaijiao.beanutil.domain;

import java.util.List;

public class JQuickSourceUser {

    private static final String staticField = "static";

    private final String finalField = "final";

    private Long id;

    private String name;

    private Integer age;

    private String email;

    private Boolean active;

    private Double score;

    private JQuickAddress address;

    private List<String> tags;

    public JQuickSourceUser() {
    }

    public JQuickSourceUser(Long id, String name, Integer age, String email, Boolean active, Double score) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.active = active;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public JQuickAddress getAddress() {
        return address;
    }

    public void setAddress(JQuickAddress address) {
        this.address = address;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
