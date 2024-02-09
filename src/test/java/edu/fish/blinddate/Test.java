package edu.fish.blinddate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fish.blinddate.entity.OneRecord;
import edu.fish.blinddate.enums.Role;
import edu.fish.blinddate.service.MainService;
import edu.fish.blinddate.utils.TemplateUtil;
import jakarta.annotation.Resource;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Test {

    @Resource
    MainService mainService;

    @org.junit.jupiter.api.Test
    public void T() throws IOException, ParseException {

        List<OneRecord> candidateRecordList = new ArrayList<>();
        OneRecord oneRecord = new OneRecord();
        oneRecord.setDate("2023-10-23");
        oneRecord.setSuccessCnt(2);
        oneRecord.setTotalCnt(2);
        oneRecord.setExplanation("上午她约了我看电影，然后中午她回去后，又约我晚上出来去打羽毛球。");
        candidateRecordList.add(oneRecord);
        oneRecord = new OneRecord();
        oneRecord.setDate("2023-10-25");
        oneRecord.setSuccessCnt(0);
        oneRecord.setTotalCnt(1);
        oneRecord.setExplanation("她约我去爬山，但是我今天不太舒服，就没有答应。");
        candidateRecordList.add(oneRecord);

        List<OneRecord> userRecordList = new ArrayList<>();
        oneRecord = new OneRecord();
        oneRecord.setDate("2023-10-28");
        oneRecord.setSuccessCnt(0);
        oneRecord.setTotalCnt(1);
        oneRecord.setExplanation("我约她去喝下午茶，但是她说约了朋友出去玩。");
        userRecordList.add(oneRecord);
        oneRecord = new OneRecord();
        oneRecord.setDate("2023-11-05");
        oneRecord.setSuccessCnt(1);
        oneRecord.setTotalCnt(1);
        oneRecord.setExplanation("我叫她去游乐场玩，她同意了。");
        userRecordList.add(oneRecord);
        // 尝试约会次数
        int candidateCnt = 3;
        int userCnt = 2;
        // 约会成功率
        BigDecimal candidateSuccessRate = BigDecimal.valueOf(0.6666);
        BigDecimal userSuccessRate = BigDecimal.valueOf(0.5000);

        StringBuilder candidateStr = new StringBuilder();
        candidateRecordList.forEach(r -> {
            candidateStr.append(TemplateUtil.singleCandidateAnalysisTemplatePart1(candidateStr, r, Role.CANDIDATE));
        });

        StringBuilder userStr = new StringBuilder();
        userRecordList.forEach(r -> {
            userStr.append(TemplateUtil.singleCandidateAnalysisTemplatePart1(userStr, r, Role.USER));
        });

        TemplateUtil.singleCandidateAnalysisTemplatePart2(candidateStr, candidateCnt, candidateSuccessRate, Role.CANDIDATE);
        TemplateUtil.singleCandidateAnalysisTemplatePart2(candidateStr, userCnt, userSuccessRate, Role.USER);

        String promptAndMSg = TemplateUtil.singleCandidateAnalysisTemplatePart3(userStr, candidateStr);

        // 定义要发送的JSON字符串
        String jsonContent = "{\"contents\":[{\"parts\":[{\"text\":\"" + promptAndMSg + "\"}]}]}";
        // 定义目标URL
        String targetUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=AIzaSyDapcwpKabenoQ7z4lHcWpavZngUm_97bg";

        HttpHost proxy = new HttpHost("localhost", 7890);
        // 创建RequestConfig，并设置代理
        RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .build();

        // 创建HttpClient实例
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();) {
            // 创建HttpPost请求
            HttpPost httpPost = new HttpPost(targetUrl);

            // 设置请求头，指定内容类型为JSON
            httpPost.setHeader("Content-Type", "application/json");

            // 设置请求体内容
            StringEntity stringEntity = new StringEntity(jsonContent, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);

            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // 处理响应
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String jsonString = EntityUtils.toString(responseEntity);

                    ObjectMapper objectMapper = new ObjectMapper();

                    try {
                        JsonNode jsonNode = objectMapper.readTree(jsonString);
                        JsonNode textNode = jsonNode.at("/candidates/0/content/parts/0/text");

                        if (textNode.isTextual()) {
                            String textValue = textNode.asText();
                            System.out.println("Text Field Value: " + textValue);
                        } else {
                            System.out.println("Text Field not found or not a valid string");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
