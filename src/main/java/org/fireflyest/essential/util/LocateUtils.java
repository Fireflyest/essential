package org.fireflyest.essential.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;
import org.fireflyest.essential.Essential;

import com.google.gson.Gson;

public class LocateUtils {
    
    private static final Map<String, String> locMap = new HashMap<>();

    private LocateUtils() {
    }

    public static String locate(String ip) {
        // 缓存
        if (locMap.containsKey(ip)) {
            return locMap.get(ip);
        }
        // 获取地址
        new BukkitRunnable() {
            public void run() {
                try {
                    String resultString = doGet(ip);
                    if (resultString != null) {
                        Result result = new Gson().fromJson(resultString, Result.class);
                        locMap.put(ip, null == result.province || "".equals(result.province) ? "麦块" : result.province);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Essential.getPlugin());
        return "麦块";
    }

    private static String doGet(String ip) throws IOException {
        String result = null;
        URL url = new URL("https://ip.useragentinfo.com/json?ip=" + ip);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // 设置连接方式：get
        connection.setConnectTimeout(15000); // 设置连接主机服务器的超时时间：15000毫秒
        connection.setReadTimeout(60000); // 设置读取远程返回的数据时间：60000毫秒
        connection.connect(); // 发送请求
        if (connection.getResponseCode() != 200) {
            connection.disconnect();
            return result;
        }
        // 读取
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){
            StringBuilder stringBuilder = new StringBuilder();
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                stringBuilder.append(temp);
                stringBuilder.append("\r\n");
            }
            result = stringBuilder.toString();
        }
        connection.disconnect();
        return result;
    }

    class Result {
        private String country;
        private String province;

        public String getCountry() {
            return country;
        }
        public void setCountry(String country) {
            this.country = country;
        }
        public String getProvince() {
            return province;
        }
        public void setProvince(String province) {
            this.province = province;
        }
    }

}
