package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.model.entity.UserCheckin;
import com.yupi.yuoj.service.UserCheckinService;
import com.yupi.yuoj.mapper.UserCheckinMapper;
import org.springframework.stereotype.Service;

/**
* @author 24402
* @description 针对表【user_checkin(用户修行打卡表)】的数据库操作Service实现
* @createDate 2026-04-16 09:37:59
*/
@Service
public class UserCheckinServiceImpl extends ServiceImpl<UserCheckinMapper, UserCheckin>
    implements UserCheckinService{

}




