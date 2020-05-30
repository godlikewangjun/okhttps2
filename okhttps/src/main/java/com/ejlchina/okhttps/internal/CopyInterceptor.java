package com.ejlchina.okhttps.internal;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.List;

public class CopyInterceptor implements Interceptor {

    public static boolean notIn(List<Interceptor> list) {
        for (Interceptor item : list) {
            if (item instanceof CopyInterceptor) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody body = response.body();
        String type = response.header("Content-Type");
        if (body == null || type != null && (type.contains("octet-stream")
                || type.contains("image") || type.contains("video")
                || type.contains("archive") || type.contains("word")
                || type.contains("xls") || type.contains("pdf"))) {
            // 若是下载文件，则必须指定在 IO 线程操作
            return response;
        }
        ResponseBody newBody = ResponseBody.create(body.contentType(), body.bytes());
        return response.newBuilder().body(newBody).build();
    }

}
