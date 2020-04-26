package com.rye.factory.net

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.UnsupportedCharsetException


/**
 * 日志拦截器
 */
class LogInterceptor : Interceptor {
    companion object {
        const val TAG = "LogInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val UTF8 = Charsets.UTF_8

        val request = chain.request()
        val requestBody = request.body()
        var reqBody: String? = null
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val contentType = requestBody.contentType()
            contentType?.charset(UTF8)
            reqBody = buffer.readString(UTF8)
        }
        Log.e(TAG, "请求方式：${request.method()} \n  请求地址：${request.url()} \n " +
                "请求头：${request.headers()} \n  请求内容：$reqBody \n")

        val response = chain.proceed(request)
        val responseBody = response.body()
        var respBody: String? = null
        if (responseBody != null) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer()
            var charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType!=null) {
                try {
                    charset = contentType.charset(UTF8) ?: UTF8
                }catch (e:UnsupportedCharsetException) {
                    e.printStackTrace()
                }
            }
            respBody = buffer.clone().readString(charset)
        }
        Log.e(TAG,"响应结果：${response.code()} -- ${response.message()} \n" +
                "请求地址：${response.request().url()} \n  请求内容：$reqBody \n 响应结果：$respBody \n")
        return response
    }
}