package org.example;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class SendCardDemo {

    // 应用密钥
    private static final String APP_KEY = "";
    private static final String APP_SECRET = "";

    // 请求地址
    private static final String AUTH_URL = "http://apiin.im.baidu.com/api/v1/auth/app_access_token";
    private static final String CARD_MSG_URL = "https://example.com/api/sendCard"; // 替换为真实URL

    private static final String RESPONSE_SUCCESS = "0"; // 根据接口文档填写成功code
    private static final String RESPONSE_CODE = "code"; // 根据接口返回字段名调整

    private static final String RESPONSE_DATA = "data";
    private static final String APP_ACCESS_TOKEN = "app_access_token";
    public static final String APP_TOKEN_PREFIX = "Bearer-";

    private static final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    public static String getAppToken() {
        if (StringUtils.isBlank(APP_KEY) || StringUtils.isBlank(APP_SECRET)){
            log.error("请配置应用密钥，详情见 https://ku.baidu-int.com/knowledge/HFVrC7hq1Q/2tsPs8CtSd/Bu7DDg4dpB/ewRrO8RmSYGIYB#anchor-60d85220-ed32-11ec-afbd-8f9f74ad5743");
            return null;
        }
        AppCertificate appCertificate = new AppCertificate(APP_KEY, MD5Encode(APP_SECRET));
        HttpEntity<AppCertificate> httpEntity = new HttpEntity<>(appCertificate);
        try {
            ResponseEntity<JSONObject> exchange = restTemplate
                    .exchange(AUTH_URL, HttpMethod.POST, httpEntity, JSONObject.class);
            JSONObject res = exchange.getBody();
            if (res == null || !RESPONSE_SUCCESS.equals(res.getString(RESPONSE_CODE))) {
                log.error("get app token from openPlatform error, res={}", res);
                return null;
            }
            String appToken = res.getJSONObject(RESPONSE_DATA).getString(APP_ACCESS_TOKEN);
            log.info("get app token from openPlatform success, appToken={}", appToken);
            return APP_TOKEN_PREFIX + appToken;
        } catch (Exception e) {
            log.error("get app token error", e);
        }
        return null;
    }

    private static void sendCardMsg(String appToken, SendCardRequest sendCardRequest) {
        HttpHeaders headers = new HttpHeaders();
        // 业务方发送时指定，用于问题定位。请传入具有唯一性的值
        headers.add("LOGID", generateLogId());
        headers.add(HttpHeaders.AUTHORIZATION, appToken);

        log.info("send card sendCardRequest, sendCardRequest={}", sendCardRequest);
        HttpEntity<SendCardRequest> httpEntity = new HttpEntity<>(sendCardRequest, headers);
        try {
            ResponseEntity<JSONObject> exchange = restTemplate
                    .exchange(CARD_MSG_URL, HttpMethod.POST, httpEntity, JSONObject.class);
            JSONObject res = exchange.getBody();
            log.info("send card res, res={}", res);
            if (res == null || !RESPONSE_SUCCESS.equals(res.getString(RESPONSE_CODE))) {
                log.error("send card msg error, res={}", res);
            }
        } catch (Exception e) {
            log.error("send card msg error", e);
        }
    }


    public static String MD5Encode(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(raw.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest();
            StringBuilder buf = new StringBuilder(32); // 预分配32位容量
            for (byte b : bytes) {
                buf.append(String.format("%02x", b & 0xFF));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public static String generateLogId() {
        UUID uuid = UUID.randomUUID();
        return Long.toString(Math.abs(uuid.getMostSignificantBits() + uuid.getLeastSignificantBits()));
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppCertificate {
        private String appKey;

        private String appSecret;
    }

    /**
     * 发送卡片接口-对象SendCardRequest
     * 包含WorkCardUserData对象和其他发卡必填参数
     * https://ku.baidu-int.com/knowledge/HFVrC7hq1Q/2tsPs8CtSd/Bu7DDg4dpB/ewRrO8RmSYGIYB
     */
    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendCardRequest {

        /**
         * 动态卡中配置动态变量集合
         * 开发者搭建平台-动态变量列表-变量预览
         */
        private WorkCardUserData userData;

        /**
         * 卡片模板ID
         */
        private String templateId;

        /**
         * true: 用户im消息，此时必须使用user-access-token
         * false：服务号消息、机器人消息
         */
        private boolean userMsg = false;

        /**
         * user： 用户
         * group：群聊
         * 服务号消息这里只能填user，机器人消息这里只能填group
         */
        private String receiverType = "user";

        /**
         * receiver_type为user时：用户的user_id（百度邮箱前缀）
         */
        private String receiverId;

        /**
         * 任意的上下文数据，互动请求回调时会原样返回（长度限制2000）
         * 必须要传入template_id，处理用户操作的回调请求时用得到
         */
        private Map<String, Object> appContext;

        /**
         * 期望后续允许客户端发起互动的有效时长（单位秒）
         * 默认值：3个月
         */
        private Integer interactivityExpire = 3600 * 24 * 90;

        /**
         * 期望后续修改消息个性化内容的有效时长（单位秒）
         * 默认值：3个月
         */
        private Integer modifyExpire = 3600 * 24 * 90;

        /**
         * 消息预览的提示文字
         */
        private String offlineNotifyTxt;

        public SendCardRequest(WorkCardUserData userData, String templateId, String receiverId){
            this.userData = userData;
            this.templateId = templateId;
            this.receiverId = receiverId;
        }
    }

    @Data
    public static class WorkCardUserData {
        private String title_321570 = "请输入_test";
        private String img = "按钮";
        private String text_126885 = "请选择";
        private String text = "输入";
        private String text_030849 = "标题";
        private String time = "12345678-c";
        private String text_652103 = "test";
        private String defaultButton = "创建";
        private List<String> text_126885_options;

        public WorkCardUserData() {
            this.text_126885_options = Arrays.asList("1", "2", "3");
        }

    }

    public static void main(String[] args) {
        // 获取appToken
        String appToken = getAppToken();
        if (StringUtils.isBlank(appToken)){
            return;
        }

        String templateId = "65d63ae01df54b809b74a4a836087ae9";
        String userId = "duyu11";
        // 构造userData
        WorkCardUserData userData = new WorkCardUserData();
        // 构造请求体
        SendCardRequest request = new SendCardRequest(userData, templateId, userId);
        // 调用发卡接口
        sendCardMsg(appToken, request);
    }
}