package com.nexus.common.utils;


import com.nexus.common.core.ip.IpHome;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ip 工具类
 *
 * @author wk
 * @date 2023/12/11
 */
@Slf4j
public class IpUtils {
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String CHINA = "中国";
    private static final String ZERO = "0";
    private static final String UN_KNOW = "unknown";
    private static final String UN_KNOW_CHINESE = "未知";
    private static final Pattern IP_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)(\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)){3}$|" +
                    "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
    );

    private static final String[] HEADERS_TO_CHECK = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Real-IP"
    };

    private IpUtils() {
    }

    /**
     * ip地址转数字
     *
     * @param ip ip地址
     * @return {@link Long}
     */
    public static Long ipToNumber(String ip) {
        String[] ipFragments = ip.split("\\.");
        long ipNumber = 0L;
        for (int i = 0; i < ipFragments.length; i++) {
            ipNumber += (Long.parseLong(ipFragments[i])) * (int) Math.pow(256, (3 - i));
        }
        return ipNumber;
    }

    /**
     * 获取IP地址
     *
     * @param request HttpServletRequest 请求对象
     * @return {@link String}
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = parseClientIpFromHeaders(request);

        if (!isValidIp(ip)) {
            ip = handleRemoteAddress(request);
        }

        return isValidIp(ip) ? ip : null;
    }

    /**
     * 解析客户端IP地址
     *
     * @param request HttpServletRequest 请求对象
     * @return {@link String}
     */
    private static String parseClientIpFromHeaders(HttpServletRequest request) {
        for (String header : HEADERS_TO_CHECK) {
            String ipList = request.getHeader(header);
            if (StringUtils.isBlank(ipList)) {
                continue;
            }
            String[] ips = ipList.split(",");
            for (String candidateIp : ips) {
                String trimmedIp = candidateIp.trim();
                if (isValidIp(trimmedIp)) {
                    return trimmedIp;
                }
            }
        }
        return null;
    }

    /**
     * 处理远程地址
     *
     * @param request HttpServletRequest 请求对象
     * @return {@link String}
     */
    private static String handleRemoteAddress(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if (isLocalhost(remoteAddr)) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                // 使用日志框架记录异常
                log.error("Error getting localhost IP: " + e.getMessage());
            }
        }
        return remoteAddr;
    }

    /**
     * 判断是否是本地环回地址
     *
     * @param ip ip地址
     * @return boolean
     */
    public static boolean isLocalhost(String ip) {
        return LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip);
    }

    /**
     * 判断ip是否有效
     *
     * @param ip ip地址
     * @return boolean
     */
    public static boolean isValidIp(String ip) {
        return ip != null
                && !UN_KNOW.equalsIgnoreCase(ip)
                && IP_PATTERN.matcher(ip).matches();
    }

    /**
     * 简单处理ip归属地
     * <p>
     * 国内ip，只处理到省份
     * 国外ip，只处理到国家
     *
     * @param ip 知识产权
     * @return {@link IpHome}
     */
    public static IpHome simpleIpHome(String ip) {
        IpHome ipHome = getIpHome(ip);
        if (CHINA.equals(ipHome.getCountry())) {
            if (StringUtils.isNotBlank(ipHome.getProvince()) && !ZERO.equals(ipHome.getProvince())) {
                ipHome.setResult(ipHome.getProvince());
            } else {
                ipHome.setProvince(UN_KNOW_CHINESE);
                ipHome.setResult(UN_KNOW_CHINESE);
            }
        } else {
            if (StringUtils.isNotBlank(ipHome.getCountry()) && !ZERO.equals(ipHome.getCountry())) {
                ipHome.setResult(ipHome.getCountry());
            } else {
                if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
                    ipHome.setResult(ipHome.getIpOperator());
                } else {
                    ipHome.setCountry(UN_KNOW_CHINESE);
                    ipHome.setResult(UN_KNOW_CHINESE);
                }
            }
        }
        return ipHome;
    }

    /**
     * 处理ip归属地
     * <p>
     * 国内ip，处理到 国家、省、市、运营商
     * 国外ip，处理到 国家
     *
     * @param ip 知识产权
     * @return {@link String}
     */
    public static IpHome completeIpHome(String ip) {
        IpHome ipHome = getIpHome(ip);
        if (CHINA.equals(ipHome.getCountry())) {
            if (StringUtils.isNotBlank(ipHome.getProvince()) && !ZERO.equals(ipHome.getProvince())) {
                ipHome.setResult(ipHome.getProvince() + "-" + ipHome.getCity() + "-" + ipHome.getIpOperator());
            } else {
                ipHome.setProvince(UN_KNOW_CHINESE);
                ipHome.setCity(UN_KNOW_CHINESE);
                ipHome.setIpOperator(UN_KNOW_CHINESE);
                ipHome.setResult(UN_KNOW_CHINESE);
            }
        } else {
            if (StringUtils.isNotBlank(ipHome.getCountry()) && !ZERO.equals(ipHome.getCountry())) {
                ipHome.setResult(ipHome.getCountry());
            } else {
                if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
                    ipHome.setResult(ipHome.getIpOperator());
                } else {
                    ipHome.setCountry(UN_KNOW_CHINESE);
                    ipHome.setResult(UN_KNOW_CHINESE);
                }
            }
        }
        return ipHome;
    }

    /**
     * 获取ip归属地
     *
     * @param ip 知识产权
     * @return {@link String}
     */
    public static IpHome getIpHome(String ip) {
        // 1. 创建 ip 归属对象
        IpHome ipHome = new IpHome();
        ipHome.setIp(ip);
        // 2、创建 searcher 对象
        String dbPath = "ip2region.xdb";
        Searcher searcher = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        // 1、查询
        try {
            ClassPathResource resource = new ClassPathResource(dbPath);
            inputStream = resource.getInputStream();
            baos = new ByteArrayOutputStream();
            // 将 ip2region.db 文件读取到字节数组输出流
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // 字节输出流转化为字节数组
            byte[] bytes = baos.toByteArray();
            searcher = Searcher.newWithBuffer(bytes);
            String region = searcher.search(ip);
            if (StringUtils.isBlank(region)) {
                return ipHome;
            }
            List<String> cityList = Arrays.asList(region.split("\\|"));
            if (CollectionUtils.isNotEmpty(cityList)) {
                // 国内的显示到具体的省
                ipHome.setCountry(cityList.get(0));
                ipHome.setProvince(cityList.get(2));
                ipHome.setCity(cityList.get(3));
                ipHome.setIpOperator(cityList.get(4));
            }
            return ipHome;
        } catch (Exception e) {
            log.error("ip 查找失败({}): {}\n", ip, e.getMessage());
        } finally {
            if (ObjectUtils.isNotNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if (ObjectUtils.isNotNull(baos)) {
                try {
                    baos.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if (ObjectUtils.isNotNull(searcher)) {
                // 3、关闭资源
                try {
                    searcher.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return ipHome;
    }
}
