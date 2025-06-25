package com.hrc.yukaokao.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hrc.yukaokao.annotation.AuthCheck;
import com.hrc.yukaokao.common.BaseResponse;
import com.hrc.yukaokao.common.DeleteRequest;
import com.hrc.yukaokao.common.ErrorCode;
import com.hrc.yukaokao.common.ResultUtils;
import com.hrc.yukaokao.config.VipSchedulerConfig;
import com.hrc.yukaokao.constant.UserConstant;
import com.hrc.yukaokao.exception.BusinessException;
import com.hrc.yukaokao.exception.ThrowUtils;
import com.hrc.yukaokao.manager.AiManager;
import com.hrc.yukaokao.model.dto.aiRequest.AiGenerateQuestionRequest;
import com.hrc.yukaokao.model.dto.questionRequest.*;
import com.hrc.yukaokao.model.entity.App;
import com.hrc.yukaokao.model.entity.Question;
import com.hrc.yukaokao.model.entity.User;
import com.hrc.yukaokao.model.enums.ApplyTypeEnum;
import com.hrc.yukaokao.model.vo.AppVO;
import com.hrc.yukaokao.model.vo.QuestionVO;
import com.hrc.yukaokao.service.AppService;
import com.hrc.yukaokao.service.QuestionService;
import com.hrc.yukaokao.service.UserService;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 题目表接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;
    @Resource
    private AiManager aiManager;

    @Resource
    private VipSchedulerConfig vipSchedulerConfig;
    // region 增删改查

    /**
     * 创建题目表
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        //  在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<QuestionContentDTO> questionContent = questionAddRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContent));
        // 数据校验
        questionService.validQuestion(question, true);
        // 填充默认值
        User loginUser = userService.getLoginUser(request);
        question.setUserid(loginUser.getId());
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserid().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目表（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //  在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<QuestionContentDTO> questionContent = questionUpdateRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContent));
        // 数据校验
        questionService.validQuestion(question, false);
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目表（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取题目表列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取题目表列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题目表列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserid(StrUtil.toString(loginUser.getId()));
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑题目表（给用户使用）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<QuestionContentDTO> questionContent = questionEditRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContent));
        // 数据校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserid().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    //region
    //系统的ai prompt 内容
    public static final String SYSTEM_MESSAGE="你是一位严谨的出题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "应用类别，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复\n" +
            "2. 严格按照下面的 json 格式输出题目和选项\n" +
            "```\n" +
            "[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\"},{\"value\":\"\",\"key\":\"B\"}],\"title\":\"题目标题\"}]\n" +
            "```\n" +
            "title \u200F是题目，options \u2062是选项，每个选项的 ke\u2060y 按照英文字母序（比如\u061C A、B、C、D）以此类\u061C推，value 是选项内容\n" +
            "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
            "4. 返回的题目列表格式必须为 JSON 数组\n";

    @PostMapping("/api_generate")
    public BaseResponse<List<QuestionContentDTO>> aiGenerateQuestion(
            @RequestBody AiGenerateQuestionRequest aiGenerateQuestionRequest){
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null ,ErrorCode.PARAMS_ERROR);
        Long appId = aiGenerateQuestionRequest.getAppId();
        ThrowUtils.throwIf(appId == 0
                , ErrorCode.PARAMS_ERROR);
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null , ErrorCode.NOT_FOUND_ERROR);

        //开始根据题目生成用户的prompt
        String userMessage = doGenerateQuestionUserMessage(app, aiGenerateQuestionRequest.getQuestionNumber(),
                aiGenerateQuestionRequest.getOptionNumber());
        String result = aiManager.doSyncUnStableRequest(SYSTEM_MESSAGE, userMessage);

        //还需要截取到对应的 json 格式
        int start = result.indexOf('[');
        int end = result.lastIndexOf(']');

        result = result.substring(start,end+1);
        return  ResultUtils.success(JSONUtil.toList(result,QuestionContentDTO.class));
    }

    @GetMapping("/api_generate/sse")
    public SseEmitter aiGenerateStreamQuestion(
            AiGenerateQuestionRequest aiGenerateQuestionRequest,HttpServletRequest request){
        //判断请求参数是否为空
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null ,ErrorCode.PARAMS_ERROR);
        //获取请求参数中的appId
        Long appId = aiGenerateQuestionRequest.getAppId();
        //判断appId是否为0
        ThrowUtils.throwIf(appId == 0
                , ErrorCode.PARAMS_ERROR);
        //根据appId获取App对象
        App app = appService.getById(appId);
        //判断App对象是否为空
        ThrowUtils.throwIf(app == null , ErrorCode.NOT_FOUND_ERROR);

        //开始根据题目生成用户的prompt
        String userMessage = doGenerateQuestionUserMessage(app, aiGenerateQuestionRequest.getQuestionNumber(),
                aiGenerateQuestionRequest.getOptionNumber());
        //ai 生成流式返回
        Flowable<ModelData> modelDataFlowable = aiManager
                .doSyncStreamStableRequest(SYSTEM_MESSAGE, userMessage);
        //设置为一直等待
        SseEmitter sseEmitter = new SseEmitter(0L);
        //完整的题目
        StringBuffer stringBuffer =  new StringBuffer();
        // 左括号计数器，除了默认值外，当回归为0 时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger();
        //生成线程工厂方式
        Scheduler scheduler = Schedulers.io();
        User loginUser = userService.getLoginUser(request);
        if("admin".equals(loginUser.getUserRole())){
            //vip用户提供定制的线程池
            scheduler = vipSchedulerConfig.vipScheduler();
        }
        //需要将那些多余的空格排除
        modelDataFlowable
                //数据流订阅和调度，表示将后续的操作在IO 调度器上执行，适用于 I/O密集型任务
                .observeOn(scheduler)
                //数据映射与处理
                //获取到流式的数据
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                //将数据的 空格去掉
                .map(message -> message.replaceAll("\\s",""))
                //判断每个字符是否为空
                .filter(c -> StrUtil.isNotBlank(c))
                //不为空的字符串，进行拼接成一个 Collection，便于后续的流式处理
                .flatMap(message ->{
                    //将字符串转换为 List<Character>，因为List是继承于 Iterable 的
                    List<Character> charList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        charList.add(c);
                    }
                    //这样才能通过 fromIterable 来订阅
                    return Flowable.fromIterable(charList);
                })
                //数据流订阅后，进行的操作
                .doOnNext(c ->{
                    // 识别第一个 { 表示开始 AI 传输 json 数据，打开 flag 开始拼接 json 数据
                    if( c == '{'){
                        counter.addAndGet(1);
                    }
                    if(counter.get()>0){
                        stringBuffer.append(c);
                    }
                    // 识别第一个 } 表示结束 AI 传输 json 数据
                    if(c == '}'){
                        counter.addAndGet(-1);
                        if(counter.get() == 0){
                            //积累单套题目满足 json 格式后，sse推动至前端
                            //sse 需要压缩成当行 json，sse 无法识别换行
                            sseEmitter.send(JSONUtil.toJsonStr(stringBuffer.toString()));
                            //清空 stringBuffer
                            stringBuffer.setLength(0);
                        }
                    }

                })
                .doOnError(e -> log.error(e.toString()))
                .doOnComplete(() -> sseEmitter.complete())
                //开启订阅观察
                .subscribe();
        return sseEmitter;
    }

    /**
     * 用户分为普通用户喝vip用户线程池的隔离起来（测试类接口）
     *
     * @param aiGenerateQuestionRequest
     * @return
     */
    @GetMapping("/api_generate/sse/test")
    public SseEmitter aiGenerateStreamQuestionTest(
            AiGenerateQuestionRequest aiGenerateQuestionRequest, Boolean isVip) {
        //判断请求参数是否为空
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        //获取请求参数中的appId
        Long appId = aiGenerateQuestionRequest.getAppId();
        //判断appId是否为0
        ThrowUtils.throwIf(appId == 0
                , ErrorCode.PARAMS_ERROR);
        //根据appId获取App对象
        App app = appService.getById(appId);
        //判断App对象是否为空
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);

        //开始根据题目生成用户的prompt
        String userMessage = doGenerateQuestionUserMessage(app, aiGenerateQuestionRequest.getQuestionNumber(),
                aiGenerateQuestionRequest.getOptionNumber());
        //ai 生成流式返回
        Flowable<ModelData> modelDataFlowable = aiManager
                .doSyncStreamStableRequest(SYSTEM_MESSAGE, userMessage);
        //设置为一直等待
        SseEmitter sseEmitter = new SseEmitter(0L);
        //完整的题目
        StringBuffer stringBuffer = new StringBuffer();
        // 左括号计数器，除了默认值外，当回归为0 时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger();

        Scheduler scheduler = Schedulers.io();
        if (isVip) {
            scheduler = vipSchedulerConfig.vipScheduler();
        }
        //需要将那些多余的空格排除
        modelDataFlowable
                //数据流订阅和调度，表示将后续的操作在IO 调度器上执行，适用于 I/O密集型任务
                .observeOn(scheduler)
                //数据映射与处理
                //获取到流式的数据
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                //将数据的 空格去掉
                .map(message -> message.replaceAll("\\s", ""))
                //判断每个字符是否为空
                .filter(c -> StrUtil.isNotBlank(c))
                //不为空的字符串，进行拼接成一个 Collection，便于后续的流式处理
                .flatMap(message -> {
                    //将字符串转换为 List<Character>，因为List是继承于 Iterable 的
                    List<Character> charList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        charList.add(c);
                    }
                    //这样才能通过 fromIterable 来订阅
                    return Flowable.fromIterable(charList);
                })
                //数据流订阅后，进行的操作
                .doOnNext(c -> {
                    // 识别第一个 { 表示开始 AI 传输 json 数据，打开 flag 开始拼接 json 数据
                    if (c == '{') {
                        counter.addAndGet(1);
                    }
                    if (counter.get() > 0) {
                        stringBuffer.append(c);
                    }
                    // 识别第一个 } 表示结束 AI 传输 json 数据
                    if (c == '}') {
                        counter.addAndGet(-1);
                        if (counter.get() == 0) {
                            //普通用户进行睡眠，看能否与vip 用户相互独立
                            System.out.println(Thread.currentThread().getName());
                            if (!isVip) {
                                Thread.sleep(1000L);
                            }
                            //积累单套题目满足 json 格式后，sse推动至前端
                            //sse 需要压缩成当行 json，sse 无法识别换行
                            sseEmitter.send(JSONUtil.toJsonStr(stringBuffer.toString()));
                            //清空 stringBuffer
                            stringBuffer.setLength(0);
                        }
                    }

                })
                .doOnError(e -> log.error(e.toString()))
                .doOnComplete(() -> sseEmitter.complete())
                //开启订阅观察
                .subscribe();
        return sseEmitter;
    }

    /**
     * 生成用户答题的prompt 内容
     * @param app
     * @param questionNumber
     * @param optionsNumber
     * @return
     */
    private String doGenerateQuestionUserMessage(App app,int questionNumber,int optionsNumber){
        StringBuffer userMessage = new StringBuffer();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append(ApplyTypeEnum.getEnumByValue(app.getAppType()).getText() + "类").append("\n");
        userMessage.append(questionNumber).append("\n");
        userMessage.append(optionsNumber);
        return userMessage.toString();
    }
    //endregion
}
