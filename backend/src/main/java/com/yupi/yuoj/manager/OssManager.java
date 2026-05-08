package com.yupi.yuoj.manager;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.yupi.yuoj.config.AliyunOssConfig;
import java.io.File;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 对象存储操作
 */
@Component
public class OssManager {

    @Resource
    private AliyunOssConfig aliyunOssConfig;

    @Resource
    private OSS ossClient;

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(aliyunOssConfig.getBucketName(), key,
                new File(localFilePath));
        return ossClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(aliyunOssConfig.getBucketName(), key,
                file);
        return ossClient.putObject(putObjectRequest);
    }
}
