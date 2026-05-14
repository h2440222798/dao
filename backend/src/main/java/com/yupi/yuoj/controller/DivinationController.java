package com.yupi.yuoj.controller;

import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.model.dto.divination.DivinationAnalyzeRequest;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.UserDivinationRecordService;
import com.yupi.yuoj.service.UserService;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lot drawing and Liuyao divination API.
 */
@RestController
@RequestMapping("/divination")
public class DivinationController {

    @Resource
    private UserService userService;

    @Resource
    private UserDivinationRecordService userDivinationRecordService;

    @GetMapping("/my")
    public BaseResponse<List<Map<String, Object>>> getMyRecords(
            @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userDivinationRecordService.listMyRecords(loginUser.getId(), limit));
    }

    @GetMapping("/{id}")
    public BaseResponse<Map<String, Object>> getRecordDetail(@PathVariable("id") Long id,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userDivinationRecordService.getMyRecordDetail(loginUser.getId(), id));
    }

    @PostMapping("/analyze")
    public BaseResponse<Map<String, Object>> analyze(@RequestBody DivinationAnalyzeRequest request,
            HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(userDivinationRecordService.analyzeAndSave(loginUser.getId(), request));
    }
}
