package com.hrc.yukaokao.scoring;

import com.hrc.yukaokao.common.ErrorCode;
import com.hrc.yukaokao.exception.BusinessException;
import com.hrc.yukaokao.exception.ThrowUtils;
import com.hrc.yukaokao.model.entity.App;
import com.hrc.yukaokao.model.entity.UserAnswer;
import com.hrc.yukaokao.model.enums.AppScoringStrategyEnum;
import com.hrc.yukaokao.model.enums.ApplyTypeEnum;
import com.qcloud.cos.demo.AppendObjectDemo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @FileName: ScoringStrategyContext
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/18 14:55
 * @Version: 1.0.0
 */
@Component
@Deprecated
public class ScoringStrategyContext {
    @Resource
    private CoustomTestScoringStrategy coustomTestScoringStrategy;
    @Resource
    private CustomScoreScoringStrategy customScoreScoringStrategy;

    public UserAnswer doScore(List<String> choiceLists, App app){
        ApplyTypeEnum applyTypeEnum = ApplyTypeEnum.getEnumByValue(app.getAppType());
        AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(app.getScoringStrategy());
        ThrowUtils.throwIf(applyTypeEnum == null || appScoringStrategyEnum == null, ErrorCode.SYSTEM_ERROR,"应用配置有误，未找到匹配的策略");
        //根据不同的应用类别和评分策略，选项对应的策略执行
        switch (applyTypeEnum){
            case SCORE:
               switch (appScoringStrategyEnum){
                   case CUSTOM :
                       return customScoreScoringStrategy.doScore(choiceLists,app);
                   case AI:
                       break;
               }
               break;
            case TEST:
                switch (appScoringStrategyEnum){
                    case CUSTOM :
                        return coustomTestScoringStrategy.doScore(choiceLists,app);
                    case AI:
                        break;
                }
                break;
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未找到匹配的策略");

    }
}
