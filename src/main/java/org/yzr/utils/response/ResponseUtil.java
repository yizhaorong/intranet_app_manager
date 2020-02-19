package org.yzr.utils.response;


import java.util.List;

/**
 * 响应操作结果
 * <pre>
 *  {
 *      errno： 错误码，
 *      errmsg：错误消息，
 *      data：  响应数据
 *  }
 * </pre>
 *
 * <p>
 * 错误码：
 * <ul>
 * <li> 0，成功；
 * <li> 4xx，前端错误，说明前端开发者需要重新了解后端接口使用规范：
 * <ul>
 * <li> 401，参数错误，即前端没有传递后端需要的参数；
 * <li> 402，参数值错误，即前端传递的参数值不符合后端接收范围。
 * </ul>
 * <li> 5xx，后端错误，除501外，说明后端开发者应该继续优化代码，尽量避免返回后端错误码：
 * <ul>
 * <li> 501，验证失败，即后端要求用户登录；
 * <li> 502，系统内部错误，即没有合适命名的后端内部错误；
 * <li> 503，业务不支持，即后端虽然定义了接口，但是还没有实现功能；
 * <li> 504，更新数据失效，即后端采用了乐观锁更新，而并发更新时存在数据更新失效；
 * <li> 505，更新数据失败，即后端数据库更新失败（正常情况应该更新成功）。
 * </ul>
 * <li> 6xx，小商城后端业务错误码，
 * 具体见scoremall-admin-api模块的AdminResponseCode。
 * <li> 7xx，管理后台后端业务错误码，
 * 具体见scoremall-app-api模块的WxResponseCode。
 * </ul>
 */
public class ResponseUtil {
    public static BaseResponse ok() {
        BaseResponse obj = new BaseResponse();
        obj.setCode(0);
        obj.setMsg("成功");
        return obj;
    }

    public static <T> BaseResponse ok(T data) {
        BaseResponse obj = new BaseResponse();
        obj.setCode(0);
        obj.setMsg("成功");
        obj.setData(data);
        return obj;
    }

    public static <T> BaseResponse okList(List<T> list) {
        PageData pageData = new PageData();
        pageData.setList(list);
        pageData.setTotal(list.size());
        pageData.setPage(1);
        pageData.setPageSize(list.size());
        pageData.setTotalPage(1);
        return ok(pageData);
    }

    public static BaseResponse fail() {
        BaseResponse obj = new BaseResponse();
        obj.setCode(-1);
        obj.setMsg("错误");
        return obj;
    }

    public static BaseResponse fail(int errno, String errmsg) {
        BaseResponse obj = new BaseResponse();
        obj.setCode(errno);
        obj.setMsg(errmsg);
        return obj;
    }

    public static BaseResponse badArgument() {
        return fail(401, "参数不对");
    }

    public static BaseResponse badArgumentValue() {
        return fail(402, "参数值不对");
    }

    public static BaseResponse unlogin() {
        return fail(501, "请登录");
    }

    public static BaseResponse serious() {
        return fail(502, "系统内部错误");
    }

    public static BaseResponse unsupport() {
        return fail(503, "业务不支持");
    }

    public static BaseResponse updatedDateExpired() {
        return fail(504, "更新数据已经失效");
    }

    public static BaseResponse updatedDataFailed() {
        return fail(505, "更新数据失败");
    }

    public static BaseResponse unauthz() {
        return fail(506, "无操作权限");
    }
}

