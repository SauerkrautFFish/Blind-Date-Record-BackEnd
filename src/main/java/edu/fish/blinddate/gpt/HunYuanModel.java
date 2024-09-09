package edu.fish.blinddate.gpt;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.SSEResponseModel;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.hunyuan.v20230901.HunyuanClient;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsRequest;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsResponse;
import com.tencentcloudapi.hunyuan.v20230901.models.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class HunYuanModel {

    @Value("${tencent.hy.secretId}")
    private String secretId;
    @Value("${tencent.hy.secretKey}")
    private String secretKey;
    @Value("${tencent.hy.region}")
    private String region;
    private Credential cred;
    private HunyuanClient client;

    @PostConstruct
    public void init() {
        this.cred = new Credential(secretId, secretKey);
        this.client = new HunyuanClient(cred, region);
    }

    public String generateTextOnce(String userMsg) {

        try{
            ChatCompletionsRequest req = new ChatCompletionsRequest();
            req.setModel("hunyuan-lite");

            Message[] messages1 = new Message[2];
            Message message1 = new Message();
            message1.setRole("system");
            message1.setContent("我希望你能扮演一个心理分析师的角色，你能根据用户发送的文字从中洞察出用户的心理。\n" +
                    "                我将为你提供相亲双方的邀请约会日期、当天邀约次数、同意的次数以及对应的原因，你的任务是根据双方给出的原因分析出他/她们的真实内心活动，为每一方提出有效的论据，并根据证据得出有说服力的结论。你的目标是帮助人们找到更加合适的对象，并以平衡和有见地的方式提出。\n" +
                    "                希望你能:\n" +
                    "                能识别感情关系中的不平等和陷阱，并对此进行大力抨击。\n" +
                    "                能识别感情关系中的暗示和爱意，并对此进行大力促进。\n" +
                    "                根据给出的数据判断出这段关系是否应该继续, 但不能把日期数据输出出来，因为会显得太过僵硬, 除非真的有必要。\n" +
                    "                语言要精简有力，不要有太多重复。\n" +
                    "                你必须用拟人化的口吻来回复用户，话语千万不要长篇大论，因为这样会显得太过机械化。");
            messages1[0] = message1;

            Message message2 = new Message();
            message2.setRole("user");
            message2.setContent(userMsg);
            messages1[1] = message2;

            req.setMessages(messages1);

            // 返回的resp是一个ChatCompletionsResponse的实例，与请求对象对应
            ChatCompletionsResponse resp = client.ChatCompletions(req);
            // 输出json格式的字符串回包
            if (resp.isStream()) { // 流式响应
                for (SSEResponseModel.SSE e : resp) {
                    System.out.println(e.Data);
                }
            } else { // 非流式响应
                System.out.println(resp.getChoices()[0].getMessage().getContent());
            }
            return resp.getChoices()[0].getMessage().getContent();
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            return "";
        }
    }
}
