package browser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * HTML文本解析器
 * Created by Sylvester on 17/5/8.
 */
public class HTMLParser {

    private Document doc;

    /**
     * 实例化doc对象
     * @param url 网站地址
     */
    public HTMLParser(String url) {
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        String title = doc.title();
        if (doc.title().equals("")) // 为没有title的网页添加默认title
            title = doc.baseUri().split("http://")[1];
        return title;
    }
}
