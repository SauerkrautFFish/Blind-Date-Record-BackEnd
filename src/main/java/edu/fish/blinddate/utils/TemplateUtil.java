package edu.fish.blinddate.utils;

import edu.fish.blinddate.entity.OneRecord;
import edu.fish.blinddate.enums.Role;

import java.math.BigDecimal;

public class TemplateUtil {

    private TemplateUtil() {
        throw new UnsupportedOperationException();
    }

    public static String singleCandidateAnalysisTemplatePart1(StringBuilder stringBuilder, OneRecord oneRecord, Role role) {
        if (Role.USER.equals(role)) {
            if (stringBuilder.isEmpty()) {
                stringBuilder.append("我的约会数据:\r\n");
            }
            stringBuilder.append(String.format("在%s日, 我尝试约了正在相亲的对象%d次, 正在相亲的对象同意赴约的次数为%d次, 原因为:%s\r\n", oneRecord.getDate(), oneRecord.getTotalCnt(), oneRecord.getSuccessCnt(), oneRecord.getExplanation()));
        } else if (Role.CANDIDATE.equals(role)) {
            if (stringBuilder.isEmpty()) {
                stringBuilder.append("正在相亲的对象的约会数据:\r\n");
            }
            stringBuilder.append(String.format("在%s日, 正在相亲的对象尝试约了我%d次, 我同意赴约的次数为%d次, 原因为:%s\r\n", oneRecord.getDate(), oneRecord.getTotalCnt(), oneRecord.getSuccessCnt(), oneRecord.getExplanation()));
        }

        return "";
    }

    public static void singleCandidateAnalysisTemplatePart2(StringBuilder stringBuilder, int cnt, BigDecimal successRate, Role role) {
        if (Role.USER.equals(role)) {
            stringBuilder.append(String.format("我一共尝试约正在相亲的对象%d次, 我约正在相亲的对象的成功率为%s%%\r\n", cnt, successRate.multiply(BigDecimal.valueOf(100))));

        } else if (Role.CANDIDATE.equals(role)) {
            stringBuilder.append(String.format("正在相亲的对象一共尝试约了我%d次, 正在相亲的对象约我的成功率为%s%%\r\n", cnt, successRate.multiply(BigDecimal.valueOf(100))));
        }
    }

    public static String singleCandidateAnalysisTemplatePart3(StringBuilder text1, StringBuilder text2) {
        return getSingleCandidateAnalysisPrompt() + text1.toString() + text2.toString();
    }

    private static String getSingleCandidateAnalysisPrompt() {
        return """
                我希望你能扮演一个心理分析师的角色，你能根据用户发送的文字从中洞察出用户的心理。
                我将为你提供相亲双方的邀请约会日期、当天邀约次数、同意的次数以及对应的原因，你的任务是根据双方给出的原因分析出他/她们的真实内心活动，为每一方提出有效的论据，并根据证据得出有说服力的结论。你的目标是帮助人们找到更加合适的对象，并以平衡和有见地的方式提出。
                希望你能:
                能识别感情关系中的不平等和陷阱，并对此进行大力抨击。
                能识别感情关系中的暗示和爱意，并对此进行大力促进。
                根据给出的数据判断出这段关系是否应该继续, 但不能把日期数据输出出来，因为会显得太过僵硬, 除非真的有必要。
                语言要精简有力，不要有太多重复。
                你必须用拟人化的口吻来回复用户，话语千万不要长篇大论，因为这样会显得太过机械化。
                """;
    }
}
