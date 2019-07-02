package com.leyou.user.service;

import com.leyou.common.constants.MQConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.config.PasswordConfig;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: dzw
 * @Date: 2019/6/26 11:48
 * @Version 1.0
 */
@Service
public class UserService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    private static final String prefix_phone = "phone_code";
    /**
     * 校验用户名或者手机号唯一
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        User user = new User();

        //判断type值 决定data 类型
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //健壮性校验
        if (!StringUtils.isNoneBlank(data)) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //去数据库校验 判断是否存在
        return userMapper.selectCount(user) == 0;
    }

    /**
     * 发送验证码
     * @param phone
     */
    public void sendPhoneVerify(String phone) {
        //验证手机号的有效性
        if (!RegexUtils.isPhone(phone)) {
            throw new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }
        //生成随机验证码
        String s = RandomStringUtils.randomNumeric(6);
        //发送验证码
        HashMap<String, String> map = new HashMap<>();

        map.put("phone", phone);
        map.put("code", s);

        amqpTemplate.convertAndSend(MQConstants.Exchange.SMS_EXCHANGE_NAME
                ,MQConstants.RoutingKey.VERIFY_CODE_KEY,map);

        //把验证码 保存进redis
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        opsForValue.set(prefix_phone+phone,s,1, TimeUnit.MINUTES);
    }

    /**
     * 用户注册
     * @param  user  用户注册的数据
     * @param code  验证码
     */
    public void userRegister(User user, String code) {
        //校验验证码
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        String s = opsForValue.get(prefix_phone + user.getPhone());
        if (!s.equals(code)) {
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        //用户注册的数据判断
        if (!RegexUtils.isPhone(user.getPhone())) {
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        //将密码加密 并加盐 这里用到spring的BCryptPasswordEncoder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //将数据保存到数据库
        int count = userMapper.insertSelective(user);
        if (count != 1) {
            //写入失败
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据用户名 和密码查询用户
     * @param username
     * @param password
     * @return
     */
    public UserDTO queryUser(String username,String password) {

        Hashtable hashtable = new Hashtable();
        //判断用户名是否存在
        User user = new User();
        user.setUsername(username);
        User i = userMapper.selectOne(user);
        if (i==null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        //判断密码是否正确  这里的判断是通过工具类的 查询两个 加密完是否一样来判断的
        if (!passwordEncoder.matches(password, i.getPassword())) {
            //密码错误
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        return BeanHelper.copyProperties(i,UserDTO.class);
    }
}
