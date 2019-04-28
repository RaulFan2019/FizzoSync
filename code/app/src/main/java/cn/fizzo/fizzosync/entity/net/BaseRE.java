package cn.fizzo.fizzosync.entity.net;

import org.xutils.http.annotation.HttpResponse;

import cn.fizzo.fizzosync.network.BaseResponseParser;

/**
 * Created by Administrator on 2016/6/28.
 */
@HttpResponse(parser = BaseResponseParser.class)
public class BaseRE {

    public String result;//正确返回的内容
    public int code;//错误编号
    public String msg;//错误描述


    public BaseRE() {
    }

    public BaseRE(String result, int code, String msg) {
        super();
        this.result = result;
        this.code = code;
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
