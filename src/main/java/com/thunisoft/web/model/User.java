package com.thunisoft.web.model;

/**
 * @Author: Diodeme
 * @Date: 2019/5/14 12:49
 * @Version 1.0
 */

public class User {
    private int id;
    private String name;

    private String passwd;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {

        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getName() {

        return name;
    }
}
