package cool.drinkup.drinkup.workflow.controller.resp;

import lombok.Data;

@Data
public class CommonResp<T> {
    private T data;
    private String message;
    private Integer code;
    public static <T> CommonResp<T> success(T data) {
        CommonResp<T> resp = new CommonResp<>();
        resp.setData(data);
        resp.setMessage("success");
        resp.setCode(200);
        return resp;
    }

    public static <T> CommonResp<T> error(String message) {
        CommonResp<T> resp = new CommonResp<>();
        resp.setMessage(message);
        resp.setCode(500);
        return resp;
    }
}
