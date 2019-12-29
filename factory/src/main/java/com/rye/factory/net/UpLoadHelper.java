package com.rye.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.rye.common.utils.HashUtil;
import com.rye.factory.Factory;

import java.io.File;
import java.util.Date;


/**
 * CreateBy ShuQin
 * at 2019/12/29
 */
public class UpLoadHelper {

    private static final String TAG=UpLoadHelper.class.getSimpleName();

   private static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
   //上传的仓库名
   private static final  String BUCKET_NAME="ryecatcher";

    private static OSS getClient(){

        // 在移动端建议使用STS的方式初始化OSSClient。-----少了个参数，先用老师这个把
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAIq8qfyarbwdZx",
                "sGttQvzH0etPN4zBan2RkiXlAy1WWe");
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒。
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒。
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个。
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次。

        return new OSSClient(Factory.app(), endpoint, credentialProvider, conf);
    }

    /**
     * 上传的最终方法，成功返回一个路径
     * @param objKey  上传上去后，在服务器上的独立的key
     * @param uploadFilePath
     * @return
     */
    private static String upload(String objKey,String uploadFilePath){
       // 构造上传请求。
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, uploadFilePath);

        try{//同步请求
            OSS client=getClient();
            //开始同步上传
            PutObjectResult result= client.putObject(request);
            //得到一个外网可访问的URL地址
            String url=client.presignPublicObjectURL(BUCKET_NAME,objKey);
            Log.d(TAG,"PublicUrl is:"+url);
            return url;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传普通图片
     * @param path  本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path){
     String key=getImageObjKey(path);
     return upload(key,path);
    }

    /**
     * 上传头像
     * @param path
     * @return
     */
    public static  String uploadPortrait(String path){
        String key=getPortraitObjKey(path);
        return upload(key,path);
    }

    /**
     * 上传音频
     * @param path
     * @return
     */
    public static String uploadAudio(String path){
        String key=getAudioObjKey(path);
        return upload(key,path);
    }

    /**
     * 分月存储，避免一个文件夹存储过多
     * @return
     */
    private static String getDataString(){
        return DateFormat.format("yyyyMM",new Date()).toString();
    }

    //201912/dsasdff.jpg -------------命名格式
    private static String getImageObjKey(String path){
    String fileMd5= HashUtil.getMD5String(new File(path));
    String dateString=getDataString();
    return String.format("image/%s/%s.jpg",dateString,fileMd5);
    }
    //201912/dsasdff.jpg -------------命名格式
    private static String getPortraitObjKey(String path){
        String fileMd5= HashUtil.getMD5String(new File(path));
        String dateString=getDataString();
        return String.format("portrait/%s/%s.jpg",dateString,fileMd5);
    }
    //201912/dsasdff.mp3 -------------命名格式
    private static String getAudioObjKey(String path){
        String fileMd5= HashUtil.getMD5String(new File(path));
        String dateString=getDataString();
        return String.format("audio/%s/%s.mp3",dateString,fileMd5);
    }

}
