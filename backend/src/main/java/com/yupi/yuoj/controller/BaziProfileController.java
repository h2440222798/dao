package com.yupi.yuoj.controller;

import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.model.dto.bazi.BaziAnalyzeRequest;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.model.vo.BaziProfileVO;
import com.yupi.yuoj.service.UserBaziProfileService;
import com.yupi.yuoj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Personal Bazi profile API.
 */
@RestController
@RequestMapping("/bazi")
public class BaziProfileController {

    @Resource
    private UserService userService;

    @Resource
    private UserBaziProfileService userBaziProfileService;

    @GetMapping("/my")
    public BaseResponse<BaziProfileVO> getMyBaziProfile(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userBaziProfileService.getMyProfile(loginUser.getId()));
    }

    @PostMapping("/analyze")
    public BaseResponse<BaziProfileVO> analyzeMyBazi(@RequestBody BaziAnalyzeRequest baziAnalyzeRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userBaziProfileService.analyzeAndSave(loginUser.getId(), baziAnalyzeRequest));
    }
}
