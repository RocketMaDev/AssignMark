package cn.rocket.assaignmark.gui;

import cn.rocket.assaignmark.LocalURL;

/**
 * 对话框中的提示类型枚举
 *
 * @author Rocket
 * @version 1.0.8
 * @since 1.0.8
 */
public enum HintType {
    HINT(LocalURL.ICON_HINT_PATH),
    ERROR(LocalURL.ICON_ERROR_PATH),
    DONE(LocalURL.ICON_DONE_PATH);

    private final String url;

    HintType(String _url) {
        url = _url;
    }

    public String getURL() {
        return url;
    }
}
