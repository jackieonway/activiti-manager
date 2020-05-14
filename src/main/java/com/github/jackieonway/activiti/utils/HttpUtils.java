package com.github.jackieonway.activiti.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

/**
 * 通用http发送方法
 *
 * @author Jackieonway
 */
public class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);
    private static final String RECV = "recv - {}";
    private static final String MOZILLA_4_0_COMPATIBLE_MSIE_6_0_WINDOWS_NT_5_1_SV_1 =
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)";
    private static final String KEEP_ALIVE = "Keep-Alive";
    private static final String ACCEPT = "accept";
    private static final String CONNECTION = "connection";
    private static final String USER_AGENT = "user-agent";
    private static final String UTF_8 = "utf-8";
    private static final String SSL = "SSL";

    private HttpUtils() {
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            log.info("sendGet - {}", urlNameString);
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty(ACCEPT, "*/*");
            connection.setRequestProperty(CONNECTION, KEEP_ALIVE);
            connection.setRequestProperty(USER_AGENT, MOZILLA_4_0_COMPATIBLE_MSIE_6_0_WINDOWS_NT_5_1_SV_1);
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            log.info(RECV, result);
        } catch (ConnectException e) {
            log.error("调用HttpUtils.sendGet ConnectException, url={},param={}", url, param, e);
        } catch (SocketTimeoutException e) {
            log.error("调用HttpUtils.sendGet SocketTimeoutException, url={},param={}", url, param, e);
        } catch (IOException e) {
            log.error("调用HttpUtils.sendGet IOException, url={},param={}", url, param, e);
        } catch (Exception e) {
            log.error("调用HttpsUtil.sendGet Exception, url={},param={}", url, param, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                log.error("调用in.close Exception, url={},param={}", url, param, ex);
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            String urlNameString = url + "?" + param;
            log.info("sendPost - {}", urlNameString);
            URL realUrl = new URL(urlNameString);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty(ACCEPT, "*/*");
            conn.setRequestProperty(CONNECTION, KEEP_ALIVE);
            conn.setRequestProperty(USER_AGENT, MOZILLA_4_0_COMPATIBLE_MSIE_6_0_WINDOWS_NT_5_1_SV_1);
            conn.setRequestProperty("Accept-Charset", UTF_8);
            conn.setRequestProperty("contentType", UTF_8);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            log.info(RECV, result);
        } catch (ConnectException e) {
            log.error("调用HttpUtils.sendPost ConnectException, url={},param={}", url, param, e);
        } catch (SocketTimeoutException e) {
            log.error("调用HttpUtils.sendPost SocketTimeoutException, url={},param={}", url, param, e);
        } catch (IOException e) {
            log.error("调用HttpUtils.sendPost IOException, url={},param={}", url, param, e);
        } catch (Exception e) {
            log.error("调用HttpsUtil.sendPost Exception, url={},param={}", url, param, e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                log.error("调用in.close Exception, url={},param={}", url, param, ex);
            }
        }
        return result.toString();
    }

    public static String sendSSLPost(String url, String param) {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + "?" + param;
        try {
            log.info("sendSSLPost - {}", urlNameString);
            SSLContext sc = SSLContext.getInstance(SSL);
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            URL console = new URL(urlNameString);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setRequestProperty(ACCEPT, "*/*");
            conn.setRequestProperty(CONNECTION, KEEP_ALIVE);
            conn.setRequestProperty(USER_AGENT, MOZILLA_4_0_COMPATIBLE_MSIE_6_0_WINDOWS_NT_5_1_SV_1);
            conn.setRequestProperty("Accept-Charset", UTF_8);
            conn.setRequestProperty("contentType", UTF_8);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ret = "";
            while ((ret = br.readLine()) != null) {
                if (!"".equals(ret.trim())) {
                    result.append(new String(ret.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                }
            }
            log.info(RECV, result);
            conn.disconnect();
            br.close();
        } catch (ConnectException e) {
            log.error("调用HttpUtils.sendSSLPost ConnectException, url={},param={}", url, param, e);
        } catch (SocketTimeoutException e) {
            log.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url={},param={}", url, param, e);
        } catch (IOException e) {
            log.error("调用HttpUtils.sendSSLPost IOException, url={},param={}", url, param, e);
        } catch (Exception e) {
            log.error("调用HttpsUtil.sendSSLPost Exception, url={},param={}", url, param, e);
        }
        return result.toString();
    }

    public static HttpSession getSession() {
        //获取到ServletRequestAttributes 里面有
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取到Request对象
        HttpServletRequest request = attrs.getRequest();
        //获取到Session对象
        return request.getSession();
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}