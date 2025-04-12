package com.app.project.manager;

import com.app.project.common.ErrorCode;
import com.app.project.config.CosClientConfig;
import com.app.project.exception.BusinessException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * Cos 对象存储操作
 *
 * @author 
 * @from 
 */
@Component
@Slf4j
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentDisposition("inline");
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        putObjectRequest.setMetadata(metadata);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param key 文件路径（例如：/biz/userId/xxx.png）
     */
    public void deleteObject(String key) {
        try {
            cosClient.deleteObject(cosClientConfig.getBucket(), key);
        } catch (CosClientException e) {
            log.error("删除COS对象失败，key = {}", key, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件删除失败");
        }
    }
}
