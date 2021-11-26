package cn.rocket.assaignmark.core;

public class MarkTarget {
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
