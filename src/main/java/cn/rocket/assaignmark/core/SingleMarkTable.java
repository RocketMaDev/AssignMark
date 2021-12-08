package cn.rocket.assaignmark.core;

import static cn.rocket.assaignmark.core.AssigningTable.STAGES;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class SingleMarkTable {
    public static final MarkTarget[] targets;

    static {
        targets = new MarkTarget[STAGES];
        int au = 100;
        int ad = 97;
        targets[0] = new MarkTarget(au, ad);
        for (int i = 1; i < STAGES; i++) {
            au = ad - 1;
            ad -= 3;
            targets[i] = new MarkTarget(au, ad);
        }
    }

    private final double[] originalMarks;
    private final int[] reqrStageNums;
    private int[] assignedMarks;
    private int[] stages;

    public SingleMarkTable(double[] originalMarks, int[] reqrStageNums) {
        this.originalMarks = originalMarks;
        this.reqrStageNums = reqrStageNums;
    }

    /**
     * xu-x   au-a <p>
     * ---- = ---- <p>
     * x-xd   a-ad <p>
     *
     * @param xu 当前分段分数上限
     * @param xd 当前分段分数下限
     * @param au 当前分段赋分上限
     * @param ad 当前分段赋分下限
     * @param x  当前分数
     * @return 当前赋分
     */
    public static int assigningFunc(double xu, double xd, double au, double ad, double x) {
        double mid = (xu - x) / (x - xd);
        double result = (au + ad * mid) / (1 + mid);
        return Math.toIntExact(Math.round(result));
    }

    public void searchStages() {
        stages = new int[STAGES];
        int pos = 0, buffer = 0, cachedBuffer, j;
        for (int i = 0; i < stages.length; i++) {
            if (i == stages.length - 1) {
                stages[i] = pos;
                break;
            }
            if (reqrStageNums[i] == 0) {
                stages[i] = -1;
                continue;
            }
            stages[i] = pos;
            if (reqrStageNums[i] == -1)
                break;
            pos += reqrStageNums[i] + buffer - 1;
            if (originalMarks.length <= pos) {
                stages[i + 1] = -1;
                break;
            }
            if (originalMarks[pos] == originalMarks[pos + 1]) {
                j = pos;
                while (j > 0 && originalMarks[j - 1] == originalMarks[j])
                    j--;
                cachedBuffer = buffer;
                buffer = pos - j + 1;
                pos = j;
                if (reqrStageNums[i] + cachedBuffer == buffer)
                    stages[i] = -1;
            } else {
                pos++;
                buffer = 0;
            }
        }
    }

    private int searchNextStage(int currentStage) {
        int stage = -1;
        for (int i = currentStage + 1; i < stages.length; i++) {
            if (stages[i] != -1) {
                stage = i;
                break;
            }
        }
        return stage;
    }

    public int[] assignMark() {
        if (assignedMarks != null)
            return assignedMarks;
        searchStages();
        assignedMarks = new int[originalMarks.length];
        int curtStage;
        int nextStage = searchNextStage(-1);
        double au, ad, xu, xd;
        do {
            curtStage = nextStage;
            nextStage = searchNextStage(curtStage);
            au = targets[curtStage].au();
            ad = targets[curtStage].ad();
            xu = originalMarks[stages[curtStage]];
            if (nextStage == -1) {
                xd = originalMarks[originalMarks.length - 1];
                for (int i = stages[curtStage]; i < originalMarks.length; i++)
                    assignedMarks[i] = assigningFunc(xu, xd, au, ad, originalMarks[i]);
            } else {
                xd = originalMarks[stages[nextStage] - 1];
                for (int i = stages[curtStage]; i < stages[nextStage]; i++)
                    assignedMarks[i] = assigningFunc(xu, xd, au, ad, originalMarks[i]);
            }
        } while (nextStage != -1);
        return assignedMarks;
    }
}
