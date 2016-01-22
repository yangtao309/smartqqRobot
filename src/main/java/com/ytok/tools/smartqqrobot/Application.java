package com.ytok.tools.smartqqrobot;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ytok.tools.smartqqrobot.callback.MessageCallback;
import com.ytok.tools.smartqqrobot.client.SmartQQClient;
import com.ytok.tools.smartqqrobot.constant.Constants;
import com.ytok.tools.smartqqrobot.constant.HttpUtil;
import com.ytok.tools.smartqqrobot.constant.PropertiesLoader;
import com.ytok.tools.smartqqrobot.model.DiscussMessage;
import com.ytok.tools.smartqqrobot.model.Group;
import com.ytok.tools.smartqqrobot.model.GroupMessage;
import com.ytok.tools.smartqqrobot.model.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author XieEnlong
 * @date 2015/12/18.
 */
public class Application {
    private static final String msgFormat = "测试@";
    private static SmartQQClient client;
    public static void main(String[] args) {
        // 测试smartqq
        client = new SmartQQClient();
        client.login();
        List<Group> groups = client.getGroupList();
        for (Group group : groups) {
            System.out.println("name: " + group.getName() + ", id: " + group.getId() + ", code: " + group.getCode());
            //client.sendMessageToGroup(group.getId(), msgFormat  + group.getCode());
        }

        GroupMessageCallback groupMessageCallback = new GroupMessageCallback();
        client.pollMessage(groupMessageCallback);
    }

    static class GroupMessageCallback implements MessageCallback {
        @Override
        public void onMessage(Message message) {
            System.out.println("hello");
        }

        @Override
        public void onGroupMessage(GroupMessage message) {
            // 测试图灵机器人
            try {
                String content = message.getContent();
                System.out.println(content);

                long groupId = message.getGroupId();
                long userId = message.getUserId();

                String appKey = PropertiesLoader.API_KEY;
                String info = URLEncoder.encode(content, "utf-8");
                String requestUrl = Constants.TURING_API_URL + "?key=" + appKey
                        + "&info=" + info + "&userid=" + userId;
                String result = HttpUtil.get(requestUrl);
                System.out.println(result);

                String parseString = null;
                JSONObject rootObj = JSON.parseObject(result);
                int code = rootObj.getIntValue("code");
                if (Constants.TEXT_CODE.equals(code)) {
                    parseString = rootObj.getString("text");
                } else if (Constants.LINK_CODE.equals(code)) {
                    parseString = "<a href='" + rootObj.getString("url") + "'>"
                            + rootObj.getString("text") + "</a>";
                } else if (Constants.NEWS_CODE.equals(code) || Constants.TRAIN_CODE.equals(code)
                        || Constants.FLIGHT_CODE.equals(code) || Constants.MENU_CODE.equals(code)) {
                    List<JSONObject> list = JSON.parseArray(rootObj.getString("list"), JSONObject.class);

                } else if (Constants.LENGTH_WRONG_CODE.equals(code) || Constants.KEY_WRONG_CODE.equals(code)) {
                    parseString = "我现在想一个人静一静,请等下再跟我聊天";
                } else if (Constants.EMPTY_CONTENT_CODE.equals(code)) {
                    parseString = "你不说话,我也没什么好说的";
                } else if (Constants.NUMBER_DONE_CODE.equals(code)) {
                    parseString ="我今天有点累了,明天再找我聊吧！";
                } else if (Constants.NOT_SUPPORT_CODE.equals(code)) {
                    parseString ="这个我还没学会,我以后会慢慢学的";
                } else if (Constants.UPGRADE_CODE.equals(code)) {
                    parseString ="我经验值满了,正在升级中...";
                } else if (Constants.DATA_EXCEPTION_CODE.equals(code)) {
                    parseString ="你都干了些什么,我怎么话都说不清楚了";
                }

                client.sendMessageToGroup(groupId, parseString);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDiscussMessage(DiscussMessage message) {
            System.out.println("hello");

        }
    }
}
