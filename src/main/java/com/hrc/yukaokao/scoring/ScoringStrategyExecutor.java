package com.hrc.yukaokao.scoring;

import com.hrc.yukaokao.common.ErrorCode;
import com.hrc.yukaokao.exception.BusinessException;
import com.hrc.yukaokao.exception.ThrowUtils;
import com.hrc.yukaokao.model.entity.App;
import com.hrc.yukaokao.model.entity.UserAnswer;
import com.hrc.yukaokao.model.enums.AppScoringStrategyEnum;
import com.hrc.yukaokao.model.enums.ApplyTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @FileName: ScoringStrategyExecutor
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/18 16:18
 * @Version: 1.0.0
 */
@Service
public class ScoringStrategyExecutor {

    /**
     * 这个怎么实例化到我们自定义的测评，得分策略呢？
     * 是因为注解：@ScoringStrategyConfig：它里面存在 @Component注解，并且他们都实现 ScoringStrategy这个类
     * 所以就会实例化他们出来
     */
    @Resource
    private  List<ScoringStrategy> scoringStrategyList;

    /**
     * 全局评分的执行器
     * @param choiceList
     * @param app
     * @return
     * @throws Exception
     */
    public UserAnswer doScore(List<String> choiceList, App app)throws Exception{
        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();

        ApplyTypeEnum applyTypeEnum = ApplyTypeEnum.getEnumByValue(appType);
        AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(appScoringStrategy);
        ThrowUtils.throwIf(applyTypeEnum == null || appScoringStrategyEnum == null, ErrorCode.SYSTEM_ERROR,"应用配置有误，未找到匹配的策略");

        //根据注解获取策略
        for (ScoringStrategy scoringstrategy : scoringStrategyList) {
            ScoringStrategyConfig scoringStrategyConfig = scoringstrategy.getClass().getAnnotation(ScoringStrategyConfig.class);
            if(scoringStrategyConfig.applyType() == appType && scoringStrategyConfig.scoringStrategy() == appScoringStrategy){
                return scoringstrategy.doScore(choiceList,app);
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"应用配置有误，未找到匹配的策略");
    }
}
