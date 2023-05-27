package xxs.common.module.crawler;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author issuser
 */
public class DouYinParseUtil {
    private static final String ABSOLUTE_ADDRESS_FOLDER = "D:\\test\\toCopyTest1";

    private static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.41");
        headers.put("origin", "https://www.douyin.com");
        //TODO cookie可能会变的吧
//        headers.put("Cookie", "ttwid=1%7C1nkM2JZy9KS2u9Z_Q6WCaqUZnq1VTBcwwJpzNqvIxT0%7C1665055333%7C5044f9ac08822040d011689f15314359e103c12e3773fbe79ec9b9a0acce289d; passport_csrf_token=8459cb7d2458d506479afb53506ba377; passport_csrf_token_default=8459cb7d2458d506479afb53506ba377; s_v_web_id=verify_lhm0amfj_XQdEKWuQ_NHls_4R08_Bw4u_mdMw7CUB3CcV; VIDEO_FILTER_MEMO_SELECT=%7B%22expireTime%22%3A1684597030957%2C%22type%22%3A1%7D; pwa2=%222%7C0%22; download_guide=%223%2F20230514%22; strategyABtestKey=%221684497390.312%22; passport_assist_user=CjzLjQKKaQ64xBoD9NIz5I9UlzW1EtVK0_qghvDN_EDQ5p2ZAdx_8FyDVmW4frbCwn3saH_BJCs0mLrFc3UaSAo89mJ9ysJ1iOJj5mLT7r0Tst1GFA6wSQbqGLFNzv4eqnDvX9Gue_lfBfUDnyJWPJPLDX4PpsKsTE5E-VSLEL_FsQ0Yia_WVCIBA01dewE%3D; n_mh=hvnJEQ4Q5eiH74-84kTFUyv4VK8xtSrpRZG1AhCeFNI; sso_uid_tt=661bee349a525a01e43ff77b5255b8c5; sso_uid_tt_ss=661bee349a525a01e43ff77b5255b8c5; toutiao_sso_user=247cc288d167cb287a4a58d9333f8f2e; toutiao_sso_user_ss=247cc288d167cb287a4a58d9333f8f2e; sid_ucp_sso_v1=1.0.0-KDUwMGU1ZThmY2ZmYzU1ZGRiNjYyYzk1MzFiMGFjMWMwYmU5M2JmMWYKHQiWyueX3QEQvcmdowYY7zEgDDDr4vLHBTgGQPQHGgJobCIgMjQ3Y2MyODhkMTY3Y2IyODdhNGE1OGQ5MzMzZjhmMmU; ssid_ucp_sso_v1=1.0.0-KDUwMGU1ZThmY2ZmYzU1ZGRiNjYyYzk1MzFiMGFjMWMwYmU5M2JmMWYKHQiWyueX3QEQvcmdowYY7zEgDDDr4vLHBTgGQPQHGgJobCIgMjQ3Y2MyODhkMTY3Y2IyODdhNGE1OGQ5MzMzZjhmMmU; odin_tt=782141e25b625f9e4e87d2828f4fea03a445c93507d807172207411eee9c27f295ad1e84f9d7defe5a39bba21c25c32a; passport_auth_status=6b8c7497a459a6c30e44fc52a97d635e%2C; passport_auth_status_ss=6b8c7497a459a6c30e44fc52a97d635e%2C; uid_tt=70e55d4076814fcb09d49ae3698946ba; uid_tt_ss=70e55d4076814fcb09d49ae3698946ba; sid_tt=b5662e1e3c4e6683a7b53e0d71a47234; sessionid=b5662e1e3c4e6683a7b53e0d71a47234; sessionid_ss=b5662e1e3c4e6683a7b53e0d71a47234; LOGIN_STATUS=1; sid_guard=b5662e1e3c4e6683a7b53e0d71a47234%7C1684497602%7C5183998%7CTue%2C+18-Jul-2023+12%3A00%3A00+GMT; sid_ucp_v1=1.0.0-KDg0YTk2OTA5ZWRlYWI5MmYwZDUzODI1YTEzYzdjMzUzZDU1NjhjMjMKGQiWyueX3QEQwsmdowYY7zEgDDgGQPQHSAQaAmxxIiBiNTY2MmUxZTNjNGU2NjgzYTdiNTNlMGQ3MWE0NzIzNA; ssid_ucp_v1=1.0.0-KDg0YTk2OTA5ZWRlYWI5MmYwZDUzODI1YTEzYzdjMzUzZDU1NjhjMjMKGQiWyueX3QEQwsmdowYY7zEgDDgGQPQHSAQaAmxxIiBiNTY2MmUxZTNjNGU2NjgzYTdiNTNlMGQ3MWE0NzIzNA; store-region=cn-zj; store-region-src=uid; d_ticket=2c48481a00318605436b1ecad4586b5a15f3b; msToken=R7WslQl_ubd4MJALMh45eoJ0Izz8Vdvp03mjY-elU-2Ne8-k6ftcgdnyVGYCLjsmI5V8MGFaW0JUC2BdHrziS6CEuAJ5V0kEyZUDJoLc0IhAnF32TtIE_dg=; FOLLOW_LIVE_POINT_INFO=%22MS4wLjABAAAAZZoSR1AZDLkSp5YyEpVDTcVQHYppVQzFnxa0hcjGHKw%2F1684512000000%2F0%2F0%2F1684498445128%22; FOLLOW_NUMBER_YELLOW_POINT_INFO=%22MS4wLjABAAAAZZoSR1AZDLkSp5YyEpVDTcVQHYppVQzFnxa0hcjGHKw%2F1684512000000%2F0%2F1684497845128%2F0%22; home_can_add_dy_2_desktop=%221%22; publish_badge_show_info=%220%2C0%2C0%2C1684497948398%22; tt_scid=F1R4VMTJd9Llwlq01ZyY6MIETOnQFsDGMdvAJrmhZ.x1YYNMzKHxnloeOqA0Vv9jee82; msToken=zzmnD606Rlh99sX8i-5yt3F-yXUmz30U7LqEvRGXXR6Idxyo7VstTaXKqDL4If8qLaD_i-xO7Vv5bVRD8Fs8nbAB4siMQOtH2C0dIWdlaFSnZUcEgQNdXnk=; __ac_nonce=0646787f80045d7325e3c; __ac_signature=_02B4Z6wo00f01j.q3AwAAIDA10KNnoP9JX4.ztiAAOuri9AfGa2alfvo90RrawN4TSyzFGL78LFil.fggaCENWHuIeKumTlNHPZ5T6uB0bpc6u1XkxL5XcCh6l7d1MpxcCkkyGAEfNXvg6Wa21; __ac_referer=https://www.iesdouyin.com/");
        //这个是拷贝的web页面的cookie
        headers.put("Cookie", "douyin.com; ttwid=1%7CR_EUT8q7YBNNk7Qk8KX140XdG9uTT-Qmv1debPYvP54%7C1684510370%7C8d4bcdb89645b72cb1808d188c1066d560eda596a64b73f303fdcb700c12e8b7; passport_csrf_token=a7bea1a06ba5a11a5095866191912cfd; passport_csrf_token_default=a7bea1a06ba5a11a5095866191912cfd; s_v_web_id=verify_lhupzieo_lp9GCrf2_3Qo9_4Rvn_BLDL_QoCjSVl90INM; ttcid=4f81dc71045c437da734b9a42e1ac8af23; download_guide=%223%2F20230519%22; pwa2=%223%7C0%22; strategyABtestKey=%221684512080.8%22; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtY2xpZW50LWNzciI6Ii0tLS0tQkVHSU4gQ0VSVElGSUNBVEUgUkVRVUVTVC0tLS0tXHJcbk1JSUJEekNCdFFJQkFEQW5NUXN3Q1FZRFZRUUdFd0pEVGpFWU1CWUdBMVVFQXd3UFltUmZkR2xqYTJWMFgyZDFcclxuWVhKa01Ga3dFd1lIS29aSXpqMENBUVlJS29aSXpqMERBUWNEUWdBRTQ5cWNsVWVJSkR3NGVMMHFCUVRNdHZ2T1xyXG5VUWdrNG5XN05NU244UkQwd1JnNnRwYnFhLzVMamlBYmNyNkluTGF4NGdxajg5dkRyN1pML0xkNXZaRUNES0FzXHJcbk1Db0dDU3FHU0liM0RRRUpEakVkTUJzd0dRWURWUjBSQkJJd0VJSU9kM2QzTG1SdmRYbHBiaTVqYjIwd0NnWUlcclxuS29aSXpqMEVBd0lEU1FBd1JnSWhBUG5vTUJENE9GWEpXbnFzaGc5K1B0dWc0TTJDK1RuanpZZmY2akJCL2hYZFxyXG5BaUVBM21XL3k2VWF3Tys1NUxKVEp0YmVkTlM2TmVYSTQvbXNkOTdWYitZaGtRTT1cclxuLS0tLS1FTkQgQ0VSVElGSUNBVEUgUkVRVUVTVC0tLS0tXHJcbiJ9; SEARCH_RESULT_LIST_TYPE=%22single%22; msToken=F3zCEE4dBRzchYcByiPdX9FiAJ-MnABx9JJLzIPJ9f8zypE3ub5apcYmkSbGLpZq7zfI6giIF1qrQeNzRqEgiMe7KQwdTr86-5YsfM8MBaJY7PBCLBIIH9mxDCRTI3c=; home_can_add_dy_2_desktop=%221%22; tt_scid=EwkLbfxHvnZ-szveY.n5odCP8XMqfBvWAdko-Ol4ICYKuADjv8OCRvzvhDKuRZPJ28db; msToken=umiDbjxh6M-Vdoy1EjDcaVhtmYJ3MycV2NtKGjeFJ-3dzdBcyH-yDilA7eH6wKzY7O2AuHKFPJ-kZkgjFdWrKM2__jG6NrrGoWaTlSuTVg6kg64pW4L6; __ac_nonce=0646850af0004e036f6e1; __ac_signature=_02B4Z6wo00f01GDDm0QAAIDA4MFhB382wjxg45.AAHxgCg9ZUrmJe4wnaksMUIRokkT4GPxBMbyMq.gzEM1i0AnvYrbWFXvUKx9O7jFm-QwR9MopbK9Heoze9QjwwHqgnAYC9TZCk27b17KV1d; __ac_referer=https://www.douyin.com/video/7234006510001458464");
        return headers;
    }

    public static String getUserAgent() {
        return getHeaders().get("user-agent");
    }

    //分页获取
    public static String getPageInfo(Map<String, String> payload, String awesomeUrl) throws IOException {
        Map<String, String> headers = getHeaders();
        headers.put("referer", awesomeUrl);
        String pageUrl = "https://www.douyin.com/aweme/v1/web/aweme/post/";

        String body = Jsoup.connect(pageUrl).data(payload).headers(headers).method(Connection.Method.GET).ignoreContentType(true).execute().body();

        return body;
    }


    public static String parseUrl(String text) {
        String regex = "https://v.douyin.com[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String url = matcher.group();
            return url;
        }
        return null;
    }


    public static String getAwesomeUrl(String url) {
        Map<String, String> headers = getHeaders();
        try {
            Document document = Jsoup.connect(url).headers(headers).get();
            String location = document.location();
            document = Jsoup.connect(location).headers(headers).get();
            location = document.location();
            return location;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getJsonStr(String url) {
        try {
            String body = Jsoup.connect(url).ignoreContentType(true).execute().body();
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONObject getData(String awesomeUrl) {
        Map<String, String> headers = getHeaders();
        try {
            if (awesomeUrl.contains("?")) {
                awesomeUrl = awesomeUrl.split("\\?")[0];
            }
            //防盗链
//            headers.put("referer", "https://www.iesdouyin.com/");
            headers.put("referer", "https://www.douyin.com/");
            Document document = Jsoup
                    .connect(awesomeUrl)
                    .headers(headers)
                    .ignoreContentType(true)
                    .get();
            //获取到这个 script中的数据（里面存储了一个json，这个json中存储了视频播放地址）
            Element element = document.selectFirst("script#RENDER_DATA[type=application/json]");
            String html = element.html();
            String decodedStr = URLDecoder.decode(html, "UTF-8");
            JSONObject json = new JSONObject(decodedStr);
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getAwesomeInfo(JSONObject data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                JSONObject obj = (JSONObject) value;
                if (obj.containsKey("aweme")) {
                    return obj;
                }
            }
        }
        return null;
    }

    public static JSONObject getPostInfo(JSONObject data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                JSONObject obj = (JSONObject) value;
                if (obj.containsKey("post")) {
                    return obj;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String url = DouYinParseUtil.parseUrl("6.61 icN:/ 复制打开抖音，看看【肉肉说了算的作品】想刀一个人的眼神是藏不住的# 抖音动物图鉴  https://v.douyin.com/Urj1Wss/");
        //获取重定向路径
        String awesomeUrl = DouYinParseUtil.getAwesomeUrl(url);
        //获取到跳转后的html页面，然后拿到json数据
        JSONObject data = DouYinParseUtil.getData(awesomeUrl);
        JSONObject awesomeInfo = DouYinParseUtil.getAwesomeInfo(data);
        //下载
        download(awesomeInfo);
    }

    private static final Pattern TITLE_PATTERN = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");

    public static String replaceTitle(String title) {
        Matcher matcher = TITLE_PATTERN.matcher(title);
        // 将匹配到的非法字符以空替换
        title = matcher.replaceAll("");
        return title;
    }

    /**
     * 文件下载
     * @param awesomeInfo
     */
    private static void download(JSONObject awesomeInfo) {
        JSONObject detail = awesomeInfo.getJSONObject("aweme").getJSONObject("detail");
        String playApi = detail.getJSONObject("video").getStr("playApi");
        String title = detail.getStr("desc");
        playApi = "https:" + playApi;
        try {
            //获取视频标题
            title = replaceTitle(title);
            //获取数据
            Connection.Response document = Jsoup.connect(playApi).ignoreContentType(true).maxBodySize(0).timeout(0).execute();
            //获取响应数据
            BufferedInputStream inputStream = document.bodyStream();
            String fileSuffix = ".mp4";
            File dest = new File(ABSOLUTE_ADDRESS_FOLDER + File.separator + title + fileSuffix);
            FileOutputStream fileOutputStream = new FileOutputStream(dest);
            IoUtil.copy(inputStream, fileOutputStream);
            inputStream.close();
            fileOutputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}