package com.xx.vo.tool.bean;

public enum JdbcClass {

    ORACLE(1,"oracle.jdbc.OracleDriver"),MYSQL(2,"com.mysql.driver");


    private int index;
    private String value;

    JdbcClass(int index, String value){
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
