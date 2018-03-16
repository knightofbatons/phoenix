package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.domain.entity.Token;
import com.airchinacargo.phoenix.domain.repository.ITokenRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;


/**
 * @author ChenYu 2018 03 15
 */
@Service
public class JdServiceImpl implements IJdService {

    @Autowired
    ITokenRepository tokenRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取京东 Token 需要的参数 前四个是从配置文件读取的
     * <p>
     * clientId 京东连接 ID
     * clientSecret 京东连接密码
     * username 京东的用户名
     * password 京东的用户密码
     */
    @Value("${Jd.CLIENT_ID}")
    private String clientId;
    @Value("${Jd.CLIENT_SECRET}")
    private String clientSecret;
    @Value("${Jd.USERNAME}")
    private String rowUsername;
    @Value("${Jd.PASSWORD}")
    private String password;
    private final int JD = 1;

    /**
     * 获取京东授权并更新 token 到数据库
     *
     * @return Token
     */
    @Override
    public Token getJdToken() {

        // 京东需要的时间格式
        DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String timestamp = mediumDateFormat.format(new Date());

        // SpringBoot 2.0 暂且存在从配置文件中读取中文乱码的问题 SpringBoot 1.x 的解决办法无效 所以在这里转码
        String username = null;
        try {
            username = new String(rowUsername.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String grantType = "access_token";
        String md5Password = md5Hex(password);
        String originalSign = clientSecret + timestamp + clientId + username + md5Password + grantType + clientSecret;
        String sign = md5Hex(originalSign).toUpperCase();

        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/oauth2/accessToken")
                    .queryString("grant_type", grantType)
                    .queryString("client_id", clientId)
                    .queryString("client_secret", clientSecret)
                    .queryString("timestamp", timestamp)
                    .queryString("username", username)
                    .queryString("password", md5Password)
                    .queryString("sign", sign)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        String token = response.getBody().getObject().getJSONObject("result").toString();
        Gson gson = new Gson();
        Token jdToken = gson.fromJson(token, new TypeToken<Token>() {
        }.getType());
        jdToken.setId(JD);
        jdToken.setDate(new Date());
        try {
            tokenRepository.deleteById(JD);
        } catch (Exception e) {
            logger.info("[ getJdToken ] --> never have Jd token before");
        }
        tokenRepository.save(jdToken);
        logger.info("[ getJdToken ] -->");
        return jdToken;
    }

    /**
     * 从数据库读取储存的 Token 如果不是当天的 就刷新 并更新数据库 然后返回
     *
     * @return Token 京东的 access token 等
     */
    @Override
    public Token readJdToken() {
        // 存在直接赋值不存在就去请求
        Token yzToken = tokenRepository.findById(JD).orElseGet(() -> getJdToken());
        logger.info("[ readJdToken ] -->");
        return yzToken;
    }

    /**
     * 错误时返回的字符串
     */
    private final String FALSE = "false";

    /**
     * 用 refresh token 刷新 access token 计划添加在定时任务中每天刷新 刷新失败就重新请求
     *
     * @param refreshToken 刷新 access token 用的 token
     */
    @Override
    public void refreshJdToken(String refreshToken) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/oauth2/refreshToken")
                    .queryString("refresh_token", refreshToken)
                    .queryString("client_id", clientId)
                    .queryString("client_secret", clientSecret)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        String isSuccess = response.getBody().getObject().get("success").toString();
        if (FALSE.equals(isSuccess)) {
            logger.info("[ refreshJdToken ] --> refresh failed run getJdToken to get a new one");
            getJdToken();
        } else {
            String token = response.getBody().getObject().getJSONObject("result").toString();
            Gson gson = new Gson();
            Token jdToken = gson.fromJson(token, new TypeToken<Token>() {
            }.getType());
            jdToken.setId(JD);
            jdToken.setDate(new Date());
            tokenRepository.save(jdToken);
            logger.info("[ refreshJdToken ] --> refresh success save to database");
        }
    }
}
