package cn.rocket.assaignmark.core.event;

/**
 * <code>AMEvent</code>事件处理器类
 *
 * @author Rocket
 * @version 1.0.8
 * @since 0.9.8
 */
public interface AMEventHandler {
    /**
     * 定义如何处理传入的<code>AMEvent</code>
     *
     * @param event 传入的<code>AMEvent</code>
     * @param msg   传入的信息 <b>可能为<code>null</code></b>！
     */
    void handle(AMEvent event, String msg);
}
