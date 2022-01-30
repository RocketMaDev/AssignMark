package cn.rocket.assaignmark.core;

/**
 * 赋分目标类。
 * <p>
 * 包含当前分段最高分和最低分
 *
 * @author Rocket
 * @version 1.0.8
 * @since 0.9.8
 */
public final class MarkTarget {
    private final double au;
    private final double ad;

    public MarkTarget(double au, double ad) {
        this.au = au;
        this.ad = ad;
    }

    public double au() {
        return au;
    }

    public double ad() {
        return ad;
    }
}
