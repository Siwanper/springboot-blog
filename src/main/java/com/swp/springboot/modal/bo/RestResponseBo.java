package com.swp.springboot.modal.bo;

/**
 * 描述:
 * rest返回对象
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-25 3:58 PM
 */
public class RestResponseBo<T> {

    /**
     * 服务器响应数据
     */
    private T payload;

    /**
     * 请求是否成功
     */
    private boolean success;

    /**
     * 请求信息
     */
    private String msg;

    /**
     * 状态吗
     */
    private int code = -1;

    /**
     * 服务器响应时间
     */
    private long timestamp;

    public RestResponseBo() {
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponseBo(boolean success) {
        this.success = success;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponseBo(T payload, boolean success) {
        this.payload = payload;
        this.success = success;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponseBo(T payload, boolean success, int code) {
        this.payload = payload;
        this.success = success;
        this.code = code;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponseBo(boolean success, String msg) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.msg = msg;
    }

    public RestResponseBo(boolean success, String msg, int code) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.msg = msg;
        this.code = code;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static RestResponseBo ok(){
        return new RestResponseBo(true);
    }

    public static <T> RestResponseBo ok(T payload){
        return new RestResponseBo( payload, true);
    }

    public static RestResponseBo ok(int code){
        return new RestResponseBo(true,null,code);
    }

    public static <T> RestResponseBo ok(T payload, int code) {
        return new RestResponseBo(payload,true, code);
    }

    public static RestResponseBo fail() {
        return new RestResponseBo(false);
    }

    public static RestResponseBo fail(String msg) {
        return new RestResponseBo(false, msg);
    }

    public static RestResponseBo fail(int code) {
        return new RestResponseBo(false, null, code);
    }

    public static RestResponseBo fail(int code, String msg) {
        return new RestResponseBo(false, msg, code);
    }


}
