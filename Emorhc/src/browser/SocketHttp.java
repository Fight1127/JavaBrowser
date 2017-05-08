package browser;

import java.io.*;
import java.net.Socket;
import java.util.LinkedHashMap;

/**
 * 底层为套接字编程的Http处理
 * Created by Sylvester on 17/5/8.
 */
public class SocketHttp {

    // Http Request 请求结构
    private String method;
    private String subUrl;
    private String host;
    private int port;

    // Http Response 响应结构
    private int status;
    private String content;
    private LinkedHashMap<String, String> headers;

    // Socket Connection 套接字连接
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // 错误信息
    private String errorMsg;

    // Cookies
    private LinkedHashMap<String, String> cookies;

    public SocketHttp(String url) {
        host = url.split("://")[1].split("/")[0];
        port = 80;
        method = "GET";
        subUrl = url.split("://")[1].split("/", 2)[1];
    }

    public boolean request() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 尝试读取Cookie
            loadCookies();

            out.print(httpRequest());
            out.flush();

            boolean isReady = false;
            // Look if there is response
            // Try six times every 250 ms
            for (int tries = 1; tries <= 6; tries++) {
                if (isReady = in.ready())
                    break;
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    errorMsg = ex.getMessage();
                    return false;
                }
            }

            if (!isReady) {
                errorMsg = "连接超时啦，你可能上了假网";
                return false;
            }

            String[] header;
            // Status
            header = in.readLine().split(" ");
            if (header.length != 3) {
                errorMsg = "响应格式错误，你可能进了个假网站";
                return false;
            }
            status = Integer.parseInt(header[1]);

            // Headers
            String read;
            headers = new LinkedHashMap<>();
            while ((isReady = in.ready()) && !(read = in.readLine()).equals("")) {
                if ((header = read.split(": ", 2)).length != 2) {
                    errorMsg = "响应头部错误";
                    return false;
                }
                processHeader(header[0], header[1]);
                headers.put(header[0], header[1]);
            }

            if (!isReady) {
                errorMsg = "响应头部不完整";
                return false;
            }
            // 尝试存储Cookie
            saveCookies();

            // Content 正文数据内容
            StringBuilder contentBuilder = new StringBuilder();
            while (in.ready())
                contentBuilder.append(in.readLine()).append("\n");
            content = contentBuilder.toString();

        } catch (IOException e) {
            errorMsg = e.getMessage();
            return false;
        }

        return true;
    }

    /**
     * 加载Cookie
     */
    private void loadCookies() {
        cookies = new LinkedHashMap<>();
        FileReader fileReader;
        if (!(new File("cookies/" + host)).isFile())
            return;

        try {
            fileReader = new FileReader("cookies/" + host);

            BufferedReader in = new BufferedReader(fileReader);
            String line;
            while ((line = in.readLine()) != null) {
                String[] cookie = line.split("=", 2);
                if (cookie.length == 2)
                    cookies.put(cookie[0], cookie[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造Http请求
     */
    private String httpRequest() {
        return method + " /" + subUrl + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "User-Agent: JavaBrowser 1.0" + "\r\n" +
                "Accept-Encoding: deflate" + "\r\n" +
                cookies() +
                "\r\n";
    }

    /**
     * 构造Http请求的Cookie
     */
    private String cookies() {
        StringBuilder cookiesToSend;
        if (cookies.size() == 0)
            return "";

        cookiesToSend = new StringBuilder("Cookie: ");
        int i = 0;
        for (String cookie : cookies.keySet()) {
            if (i != 0)
                cookiesToSend.append("; ");
            cookiesToSend.append(cookie).append("=").append(cookies.get(cookie));
            i++;
        }
        cookiesToSend.append("\r\n");

        return cookiesToSend.toString();
    }

    /**
     * 头部额外处理
     * 只为特定的头部存Cookie
     *
     * @param header name of header
     */
    private void processHeader(String header, String value) {
        if (header.equals("Set-Cookie")) {
            String[] cookie = value.split(";")[0].split("=");
            // Consider empty cookies
            if (cookie.length == 1)
                cookies.put(cookie[0], "");
            else if (cookie.length == 2)
                cookies.put(cookie[0], cookie[1]);
        }
    }

    /**
     * 存储Http响应中的Cookie到本地文件
     */
    private void saveCookies() {
        File cookiesDir = new File("cookies");
        if (!cookiesDir.isDirectory())
            cookiesDir.mkdir();
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter("cookies/" + host, false));
            for (String cookie : cookies.keySet()) {
                out.write(cookie + "=" + cookies.get(cookie) + " \n");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getContent() {
        return content;
    }

    public int getStatus() {
        return status;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }
}
