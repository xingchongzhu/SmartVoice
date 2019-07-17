package com.data.smartvoice.entry;

import java.io.Serializable;

/**
 * Class for bundle result for a success http request
 */
public class BaseResult implements Serializable {
    /**
     * for normal success result is 200, otherwise has exceptions
     */
    int Code = 200;
    /**
     * Result message
     */
    /**
     * Success or Fail
     */
    String status;

    String Msg;

    String Result;

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "Code=" + Code +
                ", status='" + status + '\'' +
                ", Msg='" + Msg + '\'' +
                ", Result='" + Result + '\'' +
                '}';
    }
}
