package com.yupi.yuoj.controller;

import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.model.dto.bazi.BaziRelationAnalyzeRequest;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.model.vo.BaziRelationVO;
import com.yupi.yuoj.service.UserBaziRelationService;
import com.yupi.yuoj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Personal relationship profile API.
 */
@RestController
@RequestMapping("/bazi/relation")
public class BaziRelationController {

    @Resource
    private UserService userService;

    @Resource
    private UserBaziRelationService userBaziRelationService;

    @GetMapping("/my")
    public BaseResponse<BaziRelationVO> getMyRelation(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userBaziRelationService.getMyRelation(loginUser.getId()));
    }

    @PostMapping("/analyze")
    public BaseResponse<BaziRelationVO> analyze(@RequestBody BaziRelationAnalyzeRequest request,
            HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(userBaziRelationService.analyzeAndSave(loginUser.getId(), request));
    }
}
