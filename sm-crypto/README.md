# About project

## 简要

* 请求加解密设备生成SM2密钥对，获取公钥

* 用解密厂家的JCE对测试文件进行公钥加密

* 请求解密机设备进行私钥解密，使用diff <原始测试文件> <解密测试文件>比较

* 使用多个线程执行解密，验证并发能力（线程数，吞吐率）

## 参考

Spring Batch [Architecture](http://docs.spring.io/spring-batch/reference/htmlsingle/#springBatchArchitecture). [Github repo](https://github.com/spring-projects/spring-batch)

Spring Boot [Reference](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/). [Github repo](https://github.com/spring-projects/spring-boot)

[Java Cryptography Architecture (JCA)](http://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html)

Java Cryptography Extension (JCE) [Wikipedia](https://en.wikipedia.org/wiki/Java_Cryptography_Extension)

## ECC和SM2

[SM2椭圆曲线公钥密码算法](http://www.oscca.gov.cn/News/201012/News_1198.htm)

ECC [Wikipedia](https://en.wikipedia.org/wiki/Elliptic_curve_cryptography)

