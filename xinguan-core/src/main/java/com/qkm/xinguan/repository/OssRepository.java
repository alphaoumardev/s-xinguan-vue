package com.qkm.xinguan.repository;

import com.baomidou.mybatisplus.extension.api.R;
import com.qkm.xinguan.response.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author qiukangming
 * @version 1.0
 * @description 阿里文件存储服务接口类
 * @since 2020/10/13 15:04
 */

public interface OssRepository {
    /**
     * 创建存储空间
     */
    void createBucket();

    /**
     * 上传文件
     *
     * @param file 文件
     * @return String
     */
    String upLoad(MultipartFile file) throws Exception;

    /**
     * 下载文件
     *
     * @param fileName 文件名称
     * @throws IOException IO流异常
     */
    void downLoad(String fileName) throws IOException;

    /**
     * 列举文件
     */
    Result listFiles();

    /**
     * 删除文 *
     * @param fileName 文件名称
     */
    Result deleteFile(String fileName);
}
