package com.qkm.xinguan.repository.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qkm.xinguan.domain.entity.Image;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.oss.OssEntity;
import com.qkm.xinguan.repository.ImageRepository;
import com.qkm.xinguan.repository.OssRepository;
import com.qkm.xinguan.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.joda.time.DateTime;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author qiukangming
 * @version 1.0
 * @description 创建 OssServiceImpl 就应该把属性值装载好
 * @since 2020/10/13 15:09
 */

@Slf4j
@Service
public class OssRepositoryImpl implements OssRepository, InitializingBean {

    /**
     * OssEntity 对象
     */
    private final OssEntity ossEntity;

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    private final ImageRepository imageRepository;

    @Autowired
    public OssRepositoryImpl(OssEntity ossEntity, ImageRepository imageRepository) {
        this.ossEntity = ossEntity;
        this.imageRepository = imageRepository;
    }


    /**
     * 初始化 bean 之后需要进行的操作
     *
     * @throws Exception 异常信息
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.isNull(ossEntity)) {
            throw new Exception("OssServiceImpl Bean 装配失败!");
        }
        this.endpoint = "http://" + ossEntity.getEndpoint();
        this.accessKeyId = ossEntity.getAccessKeyId();
        this.accessKeySecret = ossEntity.getAccessKeySecret();
        this.bucketName = ossEntity.getBucketName();
    }

    @Override
    public void createBucket() {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 判断存储空间是否存在
        if (ossClient.doesBucketExist(bucketName)) {
            throw new RuntimeException(bucketName + " 在对象存储的Bucket列表中已经存在");
        }
        // 创建存储空间。
        ossClient.createBucket(bucketName);
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    @Override
    public String upLoad(MultipartFile file) throws Exception {
        //上传的地址
        String uploadUrl;
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //判断bucketName是否存在
        if (!ossClient.doesBucketExist(bucketName)) {
            //创建bucket
            ossClient.createBucket(bucketName);
            //设置bucket的属性
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }
        //获取上传的文件流
        InputStream inputStream = file.getInputStream();

        //构建日期的文件夹路径  avatar/2020/10/10/文件名
        String datePath = new DateTime().toString("yyyy/MM/dd");

        //获取上传文件的全名称
        String original = file.getOriginalFilename();

        //获取UUID  a98059d4-633c-4b55-b310-93c997cf8038
        String fileName = UUID.randomUUID().toString().replaceAll("-", "");

        //获取上传文件的扩展名
        assert original != null;
        String fileType = original.substring(original.lastIndexOf("."));

        //拼接文件名称  a98059d4633c4b55b31093c997cf8038.png
        String newName = fileName + fileType;

        //生成文件夹   avatar/2020/10/10/a98059d4633c4b55b31093c997cf8038.png
        fileName = datePath + "/" + newName;

        //如果想要实现图片预览的效果,一定要设置以下几点
        //1.设置文件的ACL(权限)  要么是公共读,要么是公共读写
        //2.一定要设置文本类型(image/jpg)
        ObjectMetadata objectMetadata = new ObjectMetadata();
        //设置公共读权限
        objectMetadata.setObjectAcl(CannedAccessControlList.PublicRead);
        String type = getcontentType(fileType);
        objectMetadata.setContentType(type);

        //每次上传得到的名字肯定是不能相同的,在java中如何让每次生成的名字不一样呢?
        //uuid  redis分布式ID 雪花算法 为了更加方便的区分,这边的文件格式yyyy/MM/dd+uuid
        ossClient.putObject(bucketName, fileName, inputStream, objectMetadata);

        // 关闭OSSClient。
        ossClient.shutdown();

        //默认十年不过期
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);

        //bucket名称  文件名   过期时间
        uploadUrl = ossClient.generatePresignedUrl(bucketName, fileName, expiration).toString();
        log.info("上传全路径: {}", uploadUrl);
        uploadUrl = uploadUrl.substring(0, uploadUrl.indexOf("?"));
        // 如果都没有问题，将文件存入Image数据表
        BufferedImage read = ImageIO.read(file.getInputStream());
        Image image = new Image();
        image.setName(fileName);
        image.setPath(uploadUrl);
        image.setMediaType(type);
        image.setSuffix(fileType);
        image.setSize(file.getSize());
        image.setWidth(read.getWidth());
        image.setHeight(read.getHeight());
        imageRepository.save(image);
        return uploadUrl;
    }

    @Override
    public void downLoad(String fileName) throws IOException {
        // <yourObjectName>从OSS下载文件时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
        OSSObject ossObject = ossClient.getObject(bucketName, fileName);
        // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
        InputStream content = ossObject.getObjectContent();
        if (content != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                log.info("{}", line);
            }
            // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            content.close();
        }
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    @Override
    public Result listFiles() {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // ossClient.listObjects返回ObjectListing实例，包含此次listObject请求的返回结果。
        ObjectListing objectListing = ossClient.listObjects(bucketName);
        // objectListing.getObjectSummaries获取所有文件的描述信息。
        List<String> fileList = new ArrayList<>();
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            log.info(" - {} (size = {}) -", objectSummary.getKey(), objectSummary.getSize());
            fileList.add(" - " + objectSummary.getKey() + " (size = " + objectSummary.getSize() + ") -");
        }

        // 关闭OSSClient。
        ossClient.shutdown();

        return Result.ok().message("获取文件列表成功").data(fileList);
    }

    @Override
    public Result deleteFile(String fileName) {
        // <yourObjectName>从OSS下载文件时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 删除文件。
        ossClient.deleteObject(bucketName, fileName);

        // 关闭OSSClient。
        ossClient.shutdown();

        // 删除数据库文件
        LambdaQueryWrapper<Image> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Image::getName, fileName);
        boolean res = imageRepository.remove(wrapper);
        if (res) {
            return Result.ok().message("删除附件成功！");
        } else {
            throw new BusinessException("OSS文件附件删除成功，数据库文件删除失败，详情请查看系统日志！");
        }
    }

    /**
     * Description: 判断OSS服务文件上传时文件的contentType
     *
     * @param FilenameExtension 文件后缀
     * @return String
     */
    public String getcontentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpg";
    }

}
