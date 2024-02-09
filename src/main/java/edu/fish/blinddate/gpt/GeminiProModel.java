package edu.fish.blinddate.gpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fish.blinddate.enums.ResponseEnum;
import edu.fish.blinddate.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Slf4j
public class GeminiProModel {

    @Value("gpt.api.key")
    private static String API_KEY;

    private static final String API_PATH = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";

    private RequestConfig config;

    HttpPost httpPost;

    public static GeminiProModel builder() {
        return new GeminiProModel();
    }

    public GeminiProModel init() {
        // 创建HttpPost请求
        httpPost = new HttpPost(getGptApi());

        // 设置请求头，指定内容类型为JSON
        httpPost.setHeader("Content-Type", "application/json");

        return this;
    }

    public GeminiProModel proxy(HttpHost proxy) {
        this.config = RequestConfig.custom()
                .setProxy(proxy)
                .build();
        return this;
    }

    private String getJsonContent(String str) {
        return "{\"contents\":[{\"parts\":[{\"text\":\"" + str + "\"}]}]}";
    }

    public String generateTextOnce(String userMsg) throws BaseException {
        // 创建HttpClient实例
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build()) {

            // 设置请求体内容
            StringEntity stringEntity = new StringEntity(getJsonContent(userMsg), ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);

            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // 处理响应
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String jsonString = EntityUtils.toString(responseEntity);

                    ObjectMapper objectMapper = new ObjectMapper();

                    JsonNode jsonNode = objectMapper.readTree(jsonString);
                    JsonNode textNode = jsonNode.at("/candidates/0/content/parts/0/text");
                    if (textNode.isTextual()) {
                        return textNode.asText();
                    }
                }
            }
        } catch (IOException e) {
            log.error("network io error. error msg:{}", e.getMessage(), e);
            throw new BaseException(ResponseEnum.GPT_CALLED_ERROR);
        } catch (ParseException e) {
            log.error("parse response error. error msg:{}", e.getMessage(), e);
            throw new BaseException(ResponseEnum.GPT_CALLED_ERROR);
        }
        log.error("uncleared error.");
        throw new BaseException(ResponseEnum.GPT_CALLED_ERROR);
    }

    private static String getGptApi() {
        return API_PATH + "?key=" + API_KEY;
    }
}
