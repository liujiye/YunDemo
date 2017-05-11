package com.demo.yun.entity;

/**
 * Created by liujiye-pc on 2017/5/10.
 */

public class Account
{
    private String account;
    private String token;

    public Account(String account, String token)
    {
        this.account = account;
        this.token = token;
    }

    public String getAccount()
    {
        return account;
    }

    public String getToken()
    {
        return token;
    }
}
