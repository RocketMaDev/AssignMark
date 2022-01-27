package cn.rocket.assaignmark.gui;

import cn.rocket.assaignmark.LocalURL;

/**
 * @author Rocket
 * @version 0.9-pre
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
