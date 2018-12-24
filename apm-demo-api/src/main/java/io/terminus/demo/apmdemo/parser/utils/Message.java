package io.terminus.demo.apmdemo.parser.utils;

import com.decibel.uasparser.UserAgentInfo;

public class Message {
    private FormParams data;
    private String tk;
    private String cid;
    private String uid;
    private String ip;
    private UserAgentInfo ua;

    public FormParams getData() { return data; }
    public String getTk() { return tk; }
    public String getCid() { return cid; }
    public String getUid() { return uid; }
    public String getIp() { return ip; }
    public UserAgentInfo getUa() { return ua; }

    public static Message parse(String msgString) throws InvalidMessageException {
        String[] parts = msgString.split("\\,",5);
        if(parts.length != 5) {
            throw new InvalidMessageException("invalid message", msgString);
        }
        Message message = new Message();
        message.tk = parts[0];
        message.cid = parts[1];
        message.uid = parts[2];
        message.ip = parts[3];
        message.data = FormParams.parse(parts[4]);
        return message;
    }
}
