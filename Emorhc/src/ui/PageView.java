package ui;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;

import browser.WebPager;

public class PageView extends JScrollPane {

    private WebPager page;
    private JEditorPane editor;
    private String url;
    private PageListener pageListener;

    public PageView() {
        page = new WebPager();

        editor = new JEditorPane();
        editor.setEditable(false);
        editor.setContentType("txt/html;charset=UTF-8");
        setViewportView(editor);
        setAutoscrolls(true);

        editor.addHyperlinkListener(e -> {
            if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
                go(e.getURL().toString());
            }
        });
    }

    /**
     * Go to the url indicated in the param.
     */
    public void go(String url) {
        if (page.go(url)) {
            if (page.getContentType() == null)
                editor.setContentType("txt/html; charset=UTF-8");
            else
                editor.setContentType(page.getContentType());
            editor.setText(page.getContent());
            this.url = url;
            if (pageListener != null)
                pageListener.pageLoaded();
        }
    }

    /**
     * Reload the current page.
     */
    public void reload() {
        page.reload();
    }

    /**
     * Stop loading.
     */
    public void stop() {
        // TODO
    }

    /**
     * Go back to the previous page.
     */
    public void back() {
        if (page.back())
            editor.setText(page.getContent());
    }

    /**
     * Go forward to the next page.
     */
    public void forward() {
        if (page.forward())
            editor.setText(page.getContent());
    }

    /**
     * Get the url page.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the history size.
     */
    public int getHistorySize() {
        return page.getHistorySize();
    }

    /**
     * Get the current page.
     */
    public int getCurrentPage() {
        return page.getCurrentPage();
    }

    /**
     * Get the status page.
     */
    public int getStatus() {
        return page.getStatus();
    }

    /**
     * Add listener to page.
     */
    public void addPageListener(PageListener pageListener) {
        this.pageListener = pageListener;
    }

    interface PageListener {
        void pageLoaded();
    }
}
