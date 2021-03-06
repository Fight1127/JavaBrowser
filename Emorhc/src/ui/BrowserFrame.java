package ui;

import browser.HTMLParser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Creates the main structure of browser
 */
public class BrowserFrame extends JFrame {

    private JPanel panelHeader, panelContent, panelFooter;
    private JButton btnLeftArrow, btnRightArrow, btnRefreshPage, btnHomePage, btnSetting;
    private JTextField txtUrl;
    private TabPanel tabs;
    private JLabel lblStatusTitle, lblStatusValue;
    private PageView page;

    /**
     * Creates an object Mainframe contains all panels of the JBrowser
     */
    public BrowserFrame() {
        // ----Title Bar
        super("JBrowser");
        ImageIcon imgIcon = new ImageIcon("src/res/browser_icon.png");
        setIconImage(imgIcon.getImage());

        // ----Header
        // Main panel
        panelHeader = new JPanel();
        panelHeader.setLayout(new FlowLayout(FlowLayout.LEADING));
        panelHeader.setBackground(new Color(0, 0, 0));//179,227,3

        // Left arrow
        btnLeftArrow = new JButton();
        btnLeftArrow.setIcon(new ImageIcon("src/res/left_arrow.png"));
        btnLeftArrow.setToolTipText("Click to go back");
        btnLeftArrow.setPreferredSize(new Dimension(50, 30));
        btnLeftArrow.setContentAreaFilled(false);
        btnLeftArrow.setBorderPainted(false);
        btnLeftArrow.setFocusPainted(false);
        btnLeftArrow.setEnabled(false);
        btnLeftArrow.addMouseListener(new BorderButton());
        btnLeftArrow.addActionListener(ae -> {
            page = (PageView) tabs.getSelectedComponent();
            int currentPage = page.getCurrentPage();

            page.back();
            btnRightArrow.setEnabled(true);
            lblStatusValue.setText("" + page.getStatus());
            if (currentPage == 1)
                btnLeftArrow.setEnabled(false);
        });

        // Right arrow
        btnRightArrow = new JButton();
        btnRightArrow.setIcon(new ImageIcon("src/res/right_arrow.png"));
        btnRightArrow.setToolTipText("Click to go forward");
        btnRightArrow.setPreferredSize(new Dimension(50, 30));
        btnRightArrow.setContentAreaFilled(false);
        btnRightArrow.setBorderPainted(false);
        btnRightArrow.setFocusPainted(false);
        btnRightArrow.setEnabled(false);
        btnRightArrow.addMouseListener(new BorderButton());
        btnRightArrow.addActionListener(ae -> {
            page = (PageView) tabs.getSelectedComponent();
            int historySize = page.getHistorySize();
            int currentPage = page.getCurrentPage();
            page.forward();

            btnLeftArrow.setEnabled(true);
            lblStatusValue.setText("" + page.getStatus());

            if ((currentPage + 2) == historySize) {
                btnRightArrow.setEnabled(false);
            }
        });

        // URL box
        txtUrl = new JTextField(35);
        Font fontUrl = new Font("Verdana", Font.BOLD, 15);
        txtUrl.setFont(fontUrl);
        txtUrl.addActionListener(ae -> {
            page = (PageView) tabs.getSelectedComponent();
            page.go(txtUrl.getText());
            lblStatusValue.setText("" + page.getStatus());
            int historySize = page.getHistorySize();
            int currentPage = page.getCurrentPage();

            if (currentPage > 0) {
                btnLeftArrow.setEnabled(true);
            } else {
                btnLeftArrow.setEnabled(false);
            }
            if (currentPage == (historySize - 1)) {
                btnRightArrow.setEnabled(false);
            } else {
                btnRightArrow.setEnabled(true);
            }

            if (page.getHistorySize() > 0)
                btnRefreshPage.setEnabled(true);

            HTMLParser parser = new HTMLParser(txtUrl.getText());
            tabs.setTabComponentAt(tabs.getSelectedIndex(), new ButtonTabComponent(parser.getTitle(), tabs));
        });

        // Refresh page
        btnRefreshPage = new JButton();
        btnRefreshPage.setIcon(new ImageIcon("src/res/refresh.png"));
        btnRefreshPage.setToolTipText("重载此页");
        btnRefreshPage.setContentAreaFilled(false);
        btnRefreshPage.setBorderPainted(false);
        btnRefreshPage.setFocusPainted(false);
        btnRefreshPage.setEnabled(false);
        btnRefreshPage.addMouseListener(new BorderButton());
        btnRefreshPage.addActionListener(ae -> {
            page = (PageView) tabs.getSelectedComponent();
            if (page.getHistorySize() > 0)
                page.reload();
            lblStatusValue.setText("" + page.getStatus());
        });

        // Home page
        btnHomePage = new JButton();
        btnHomePage.setIcon(new ImageIcon("src/res/home.png"));
        btnHomePage.setToolTipText("打开主页");
        btnHomePage.setContentAreaFilled(false);
        btnHomePage.setBorderPainted(false);
        btnHomePage.setFocusPainted(false);
        btnHomePage.addMouseListener(new BorderButton());
        btnHomePage.addActionListener(ae -> {
            page = (PageView) tabs.getSelectedComponent();
            page.go("www.hao123.com");
            txtUrl.setText("http://www.hao123.com/");
            lblStatusValue.setText("" + page.getStatus());
            btnRefreshPage.setEnabled(true);

            int historySize = page.getHistorySize();
            int currentPage = page.getCurrentPage();

            if (currentPage > 0) {
                btnLeftArrow.setEnabled(true);
            } else {
                btnLeftArrow.setEnabled(false);
            }
            if (currentPage == (historySize - 1)) {
                btnRightArrow.setEnabled(false);
            } else {
                btnRightArrow.setEnabled(true);
            }

            HTMLParser parser = new HTMLParser(txtUrl.getText());
            tabs.setTabComponentAt(tabs.getSelectedIndex(), new ButtonTabComponent(parser.getTitle(), tabs));
        });
        
       /* //Settings
        btnSetting = new JButton(); 
        btnSetting.setLayout( new FlowLayout(FlowLayout.RIGHT));
        btnSetting.setIcon(new ImageIcon ("src/res/settings.png"));
        btnSetting.setContentAreaFilled(false);
        btnSetting.setBorderPainted(false);
        btnSetting.setFocusPainted(false);
        btnSetting.addMouseListener(new BorderButton());
        */

        // Adds components
        panelHeader.add(btnLeftArrow);
        panelHeader.add(btnRightArrow);
        panelHeader.add(txtUrl);
        panelHeader.add(btnRefreshPage);
        panelHeader.add(btnHomePage);
        //panelHeader.add(btnSetting);
        add(panelHeader, BorderLayout.NORTH);


        // ----Content
        // Main panel
        panelContent = new JPanel();
        panelContent.setLayout(new GridLayout(1, 1));
        panelContent.setBackground(new Color(243, 201, 120));

        // TabPanel
        tabs = new TabPanel(() -> {
            PageView page = (PageView) tabs.getSelectedComponent();
            // Set enabled buttons
            if (page.getCurrentPage() > 0)
                btnLeftArrow.setEnabled(true);
            else
                btnLeftArrow.setEnabled(false);

            if (page.getCurrentPage() < (page.getHistorySize() - 1))
                btnRightArrow.setEnabled(true);
            else
                btnRightArrow.setEnabled(false);

            if (page.getHistorySize() == 0)
                btnRefreshPage.setEnabled(false);
            else
                btnRefreshPage.setEnabled(true);
        });

        tabs.addChangeListener(ce -> {
            page = (PageView) tabs.getSelectedComponent();

            // Set Url and close window
            if (tabs.getSelectedIndex() != (tabs.getTabCount() - 1)) {
                txtUrl.setText(page.getUrl());
            }

            if (tabs.getTabCount() == 1) {
                System.exit(0);
            }

            //Set enabled buttons
            if (page.getCurrentPage() > 0) {
                btnLeftArrow.setEnabled(true);
            } else {
                btnLeftArrow.setEnabled(false);
            }

            if (page.getCurrentPage() < (page.getHistorySize() - 1)) {
                btnRightArrow.setEnabled(true);
            } else {
                btnRightArrow.setEnabled(false);
            }

            if (page.getHistorySize() == 0) {
                btnRefreshPage.setEnabled(false);
            } else {
                btnRefreshPage.setEnabled(true);
            }
        });

        // Adds components
        panelContent.add(tabs);
        add(panelContent, BorderLayout.CENTER);

        // ----Footer
        // Main panel
        panelFooter = new JPanel();
        panelFooter.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelFooter.setBackground(new Color(0, 0, 0));

        // Page Status
        lblStatusTitle = new JLabel("页面状态：");
        lblStatusTitle.setForeground(new Color(243, 201, 120));
        lblStatusValue = new JLabel("无");
        lblStatusValue.setForeground(Color.white);

        // Adds components
        panelFooter.add(lblStatusTitle);
        panelFooter.add(lblStatusValue);
        add(panelFooter, BorderLayout.SOUTH);

        // ----Settings
        setSize(1074, 768);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(890, 200));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // ----BorderButton action

    /**
     * Class that implements the MouseAdapter button edge.
     */
    class BorderButton extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent evt) {
            JButton btn = (JButton) evt.getSource();
            if (btn.isEnabled()) {
                btn.setBorderPainted(true);
            }
        }

        @Override
        public void mouseExited(MouseEvent evt) {
            JButton btn = (JButton) evt.getSource();
            if (btn.isEnabled()) {
                btn.setBorderPainted(false);
            }
        }
    }

    // Getters and Setters

    /**
     * Get right button to go forward.
     *
     * @return JButton btnLeftArrow
     */
    public JButton getBtnLeftArrow() {
        return btnLeftArrow;
    }

    /**
     * Get left button to go back.
     *
     * @return JButton btnRightArrow
     */
    public JButton getBtnRightArrow() {
        return btnRightArrow;
    }
}
