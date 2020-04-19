package com.rye.factory.data.helper;

import android.app.Application;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;


import com.raizlabs.android.dbflow.list.IFlowCursorIterator;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.rye.catcher.common.Common;
import com.rye.catcher.common.app.zApplication;
import com.rye.catcher.utils.PicturesCompressor;
import com.rye.catcher.utils.StreamUtil;
import com.rye.factory.Factory;
import com.rye.factory.R;
import com.rye.factory.model.api.RspModel;
import com.rye.factory.model.api.message.MsgCreateModel;
import com.rye.factory.model.card.MessageCard;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.Message_Table;
import com.rye.factory.net.NetWork;
import com.rye.factory.net.RemoteService;
import com.rye.factory.net.UpLoadHelper;



import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CreateBy ShuQin
 * at 2020/1/25
 */
public class MessageHelper {
    private static final String TAG="MessageHelper";

    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    /**
     * 发送是异步的
     *
     * @param model
     */
    public static void push(final MsgCreateModel model) {
        Factory.runOnAsync(() -> { // TODO: 2020/1/29 可以换个线程池？
            //成功状态：如果是一个已经发送过的消息，则不能重新发送
            //正在发送状态：如果是一个消息正在发送，则不能重新发送
            Message message = findFromLocal(model.getId());
            if (message != null && message.getStatus() != Message.STATUS_FAILED)
                return;

            final MessageCard card = model.buildCard();
            Factory.getMessageCenter().dispatch(card);

            //---------------------------非文字类型消息处理
           if (card.getType()!=Message.TYPE_STR){//不是文字类型的
               if (!card.getContent().startsWith(UpLoadHelper.ENDPOINT)){//地址不是远程地址
                   String content;
                   switch (card.getType()){
                       case Message.TYPE_PIC://上传图片
                           content=uploadPicture(card.getContent());
                           break;
                       case Message.TYPE_AUDIO://上传语音
                           content=uploadAudio(card.getContent());
                           break;
                       default:
                           content="";
                           break;
                   }
                   if (TextUtils.isEmpty(content)){
                       card.setStatus(Message.STATUS_FAILED);
                       //时刻别忘了存储，否则监听器里没有新数据改变
                       Factory.getMessageCenter().dispatch();
                       return;//必须加，否则setContent存的是空
                   }
                   //成功则把网络路径进行替换
                   card.setContent(content);
                   Factory.getMessageCenter().dispatch(card);
                   //因为卡片的内容改变了，而我们上传到服务器是使用的model，
                   //所以model也要更改
                   model.refreshByCard();

               }
           }




            //直接发送
            RemoteService service = NetWork.remote();
            service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                @Override
                public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                    RspModel<MessageCard> rspModel = response.body();
                    if (rspModel != null && rspModel.success()) {
                        MessageCard rspCard = rspModel.getResult();
                        if (rspCard != null) {
//                                  card.setStatus(Message.STATUS_DONE);
                            Factory.getMessageCenter().dispatch(rspCard);
                        }
                    } else {
                        //检查账号是否异常
                        Factory.decodeRspCode(rspModel, null);
                        onFailure(call, null);
                    }
                }

                @Override
                public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                    card.setStatus(Message.STATUS_FAILED);
                    //失败了刷新一下界面
                    Factory.getMessageCenter().dispatch(card);
                }
            });
        });
    }

    /**
     * 将文件上传至oss，返回oss地址
     * @param path
     * @return
     */
    private static String uploadPicture(String path){
        Log.i(TAG,"Picture locale path:"+path);
        File file=new File(path);//取到本地文件
        if (file!=null){
            //进行压缩
          String cacheDir= zApplication.getCacheDirFile().getAbsolutePath();
          String tempFile=String.format("%s/image/Cache_%s.png",cacheDir, SystemClock.currentThreadTimeMillis());
          try{//上传可能有异常
              if (PicturesCompressor.compressImage(file.getAbsolutePath(),tempFile,
                      Common.Constance.MAX_UPLOAD_IMAGE_LENGTH)){//压缩成功
                  String ossPath=UpLoadHelper.uploadImage(tempFile);
                  StreamUtil.delete(tempFile);
                  return ossPath;
              }
          }catch (Exception e){
              e.printStackTrace();
          }
        }
         return null;
    }

    /**
     * 上传语音
     * @param audioPath
     * @return
     */
    private static String uploadAudio(String audioPath) {
        File file=new File(audioPath);
        if (!file.exists()||file.length()<=0){
            return  null;
        }
        return UpLoadHelper.uploadAudio(audioPath);
    }

    /**
     * 查询一个消息，这个消息是一个群中的最后一条消息
     *
     * @param groupId
     * @return
     */
    // TODO: 2020/1/29 将最后一条消息最为会话的属性 
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }

    /**
     * 查询一个消息，和一个人的最后一条
     *
     * @param userId
     * @return
     */
    public static Message findLastWithUser(String userId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()//最后一条消息的发送者不一定是我，也可能是他
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }
}
