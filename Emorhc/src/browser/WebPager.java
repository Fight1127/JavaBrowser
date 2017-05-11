package browser;

import java.util.ArrayList;

/**
 * 网页页面的相关操作
 * Created by Sylvester on 17/5/8.
 */
public class WebPager {

    private ArrayList<String> history = new ArrayList<>();
    private int currentPage;

    private SocketHttp http;
    private String url;
    private String content;

    public WebPager() {

    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return http.getHeader("Content-Type");
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getStatus() {
        return http.getStatus();
    }

    public int getHistorySize() {
        return history.size();
    }

    public boolean go(String url) {
        if (load(url)) {
            history.add(url);
            currentPage = history.size() - 1;
            System.out.println(history);
            return true;
        }
        return false;
    }

    private boolean load(String url) {
        http = new SocketHttp("http://" + url + "/");
        if (http.request()) {
            int statusHttp = http.getStatus();
            System.out.println("" + http.getStatus());
            if (statusHttp >= 200 && statusHttp <= 208)
                content = http.getContent();
            else if (statusHttp >= 300 && statusHttp <= 307)
                go(http.getHeader("Location"));
        } else {
            return false;
        }
        return true;
    }

    public boolean reload() {
        load(history.get(currentPage));
        return true;
    }

    public boolean back() {
        // Set url - history
        currentPage = getCurrentPage() - 1;
        load(history.get(currentPage));
        return true;
    }

    public boolean forward() {
        currentPage = getCurrentPage() + 1;
        load(history.get(currentPage));
        return true;
    }
}
