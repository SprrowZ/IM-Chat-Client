package com.rye.factory.model.api.message;


import com.google.gson.annotations.Expose;
import com.rye.factory.model.card.MessageCard;
import com.rye.factory.model.db.Message;
import com.rye.factory.persistence.Account;

import java.util.Date;
import java.util.UUID;


/**
 * API请求的Model格式
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class MsgCreateModel {

    private String id;

    private String content;

    private String attach;

    // 消息类型

    private int type = Message.TYPE_STR;

    // 接收者 可为空
    private String receiverId;

    // 接收者类型，群，人
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    private MsgCreateModel(){
        //随机生成一个消息id
        this.id= UUID.randomUUID().toString();
    }


    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAttach() {
        return attach;
    }

    public int getType() {
        return type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public static class Builder{
        private MsgCreateModel model;
        public Builder(){
            this.model=new MsgCreateModel();
        }
        public Builder receiver(String receiverId,int receiverType){
            model.receiverId=receiverId;
            model.receiverType=receiverType;
            return this;
        }

        public Builder content(String content,int type){
            this.model.content=content;
            this.model.type=type;
            return this;
        }
        public Builder attach(String attach){
            this.model.attach=attach;
            return this;
        }
        public MsgCreateModel build(){
            return this.model;
        }

    }

    // 当我们需要发送一个文件的时候，content刷新的问题

    private MessageCard card;

    // 返回一个Card
    public MessageCard buildCard() {
        if (card == null) {
            MessageCard card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());

            // 如果是群
            if (receiverType == Message.RECEIVER_TYPE_GROUP) {
                card.setGroupId(receiverId);
            } else {
                card.setReceiverId(receiverId);
            }

            // 通过当前model建立的Card就是一个初步状态的Card
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
            this.card = card;
        }
        return this.card;
    }

    /**
     * 把一个mMessage消息
     * @param message
     * @return
     */
    public static MsgCreateModel buildWithMessage(Message message){
        MsgCreateModel model=new MsgCreateModel();
        model.id=message.getId();
        model.content=message.getContent();
        model.type=message.getType();
        model.attach=message.getAttach();
        if (message.getReceiver()!=null){//接收者不为空，则为人发送的
            model.receiverId=message.getReceiver().getId();
            model.receiverType=Message.RECEIVER_TYPE_NONE;
        }else{
            model.receiverId=message.getGroup().getId();
            model.receiverType=Message.RECEIVER_TYPE_NONE;
        }
        return model;
    }


}
