package cn.rocket.assaignmark.gui;

import cn.rocket.assaignmark.LocalURL;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Rocket
 * @version 1.0.8
 * @since 1.0.8
 */
public class Copyright {

    public Label errLabel;

    public void licenseClicked() {
        if (browseURL(LocalURL.LICENSE_URL))
            errLabel.setVisible(true);
    }

    public void repoClicked() {
        if (browseURL(LocalURL.REPO_URL))
            errLabel.setVisible(true);
    }

    public void avatarClicked() {
        if (browseURL(LocalURL.PERSONAL_URL))
            errLabel.setVisible(true);
    }

    /**
     * 使用内建方式用系统浏览器打开网址
     *
     * @param target 需要打开的网址
     * @return <code>true</code>如果无法打开
     */
    private boolean browseURL(String target) {
        if (!Desktop.isDesktopSupported())
            return true;
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(target));
        } catch (IOException | URISyntaxException e) {
            return true;
        }
        return false;
    }
}
