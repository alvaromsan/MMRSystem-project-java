import java.util.Scanner;

public class MMRSystem {

    private static final int MAX_MMR_CHANGE = 50;
    private static final int MAX_CR_CHANGE = 30;

    // Breakpoints for MMR/CR scale
    private static final int[] DIFF_BREAKPOINTS = {0, 100, 200, 300, 400, 500};
    private static final double[] WINNER_PERCENTS_POSITIVE = {0.50, 0.40, 0.30, 0.20, 0.10, 0.00};
    private static final double[] WINNER_PERCENTS_NEGATIVE = {0.50, 0.60, 0.70, 0.80, 0.90, 1.00};

    // Catch-up mechanic breakpoints & points
    private static final int[] CATCHUP_BREAKPOINTS = {0, 25, 50, 75, 100, 125, 150, 175, 200, 225, 250};
    private static final int[] CATCHUP_POINTS_POSITIVE = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static final int[] CATCHUP_POINTS_NEGATIVE = {0, -1, -2, -3, -4, -5, -6, -7, -8, -9, -10};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Team A MMR: ");
        int teamAMMR = scanner.nextInt();

        System.out.print("Enter Team A CR: ");
        int teamACR = scanner.nextInt();

        System.out.print("Enter Team B MMR: ");
        int teamBMMR = scanner.nextInt();

        System.out.print("Enter Team B CR: ");
        int teamBCR = scanner.nextInt();

        scanner.close();

        int mmrDiff = teamAMMR - teamBMMR;

        System.out.println("\n=== If Team A Wins ===");
        applyAndPrintResults(teamAMMR, teamACR, teamBMMR, teamBCR, mmrDiff, true);

        System.out.println("\n=== If Team B Wins ===");
        applyAndPrintResults(teamAMMR, teamACR, teamBMMR, teamBCR, mmrDiff, false);
    }

    private static void applyAndPrintResults(
            int teamAMMR, int teamACR, int teamBMMR, int teamBCR,
            int mmrDiff, boolean isTeamAWin) {

        // ----------- MMR calculation -----------
        double winnerPercentMMR = getWinnerPercent(isTeamAWin ? mmrDiff : -mmrDiff);
        int mmrChange = (int) Math.round(winnerPercentMMR * MAX_MMR_CHANGE);

        int teamAMMRResult = isTeamAWin ? teamAMMR + mmrChange : teamAMMR - mmrChange;
        int teamBMMRResult = isTeamAWin ? teamBMMR - mmrChange : teamBMMR + mmrChange;

        // ----------- CR calculation -----------

        // Team A CR base change
        int teamACRdiff = teamACR - teamBMMR;
        double winnerPercentCR_A = getWinnerPercent(isTeamAWin ? teamACRdiff : -teamACRdiff);
        int baseCrChangeA = (int) Math.round(winnerPercentCR_A * MAX_CR_CHANGE);
        // Assign sign based on whether Team A won or lost
        baseCrChangeA = isTeamAWin ? baseCrChangeA : -baseCrChangeA;

        // Team B CR base change
        int teamBCRdiff = teamBCR - teamAMMR;
        double winnerPercentCR_B = getWinnerPercent(isTeamAWin ? -teamBCRdiff : teamBCRdiff);
        int baseCrChangeB = (int) Math.round(winnerPercentCR_B * MAX_CR_CHANGE);
        // Assign sign based on whether Team B won or lost
        baseCrChangeB = isTeamAWin ? -baseCrChangeB : baseCrChangeB;

        // Calculate catch-up adjustments
        int catchUpA = getCatchUpPoints(teamAMMR - teamACR);
        int catchUpB = getCatchUpPoints(teamBMMR - teamBCR);

        // Apply catch-up mechanic:
        int crChangeA = baseCrChangeA + catchUpA;
        int crChangeB = baseCrChangeB + catchUpB;

        // Checking:
        // 1) Winning team is not losing points >> make it 0
        // 2) Losing team is not winning points >> make it 0
        crChangeA = isTeamAWin ? Math.max(crChangeA, 0) : Math.min(crChangeA, 0);
        crChangeB = isTeamAWin ? Math.min(crChangeB, 0) : Math.max(crChangeB, 0);

        // --- Grace system ---

        int mmrCrDiffA = teamAMMR - teamACR;
        int mmrCrDiffB = teamBMMR - teamBCR;

        // For Team A:
        if (mmrCrDiffA >= 200 && !isTeamAWin) {
            // Team A lost but MMR-CR > 200 → no CR loss allowed
            crChangeA = 0;
        } else if (mmrCrDiffA <= -200 && isTeamAWin) {
            // Team A won but MMR-CR < -200 → no CR gain allowed
            crChangeA = 0;
        }

        // For Team B:
        if (mmrCrDiffB >= 200 && isTeamAWin) {
            // Team B lost but MMR-CR > 200 → no CR loss allowed
            crChangeB = 0;
        } else if (mmrCrDiffB <= -200 && !isTeamAWin) {
            // Team B won but MMR-CR < -200 → no CR gain allowed
            crChangeB = 0;
        }

        // Final CR results:
        int teamACRResult = teamACR + crChangeA;
        int teamBCRResult = teamBCR + crChangeB;

        System.out.println("Team A: MMR " + teamAMMR + " -> " + teamAMMRResult + ", CR " + teamACR + " -> " + teamACRResult);
        System.out.println("Team B: MMR " + teamBMMR + " -> " + teamBMMRResult + ", CR " + teamBCR + " -> " + teamBCRResult);
    }

    private static double getWinnerPercent(int diff) {
        boolean positive = diff >= 0;
        int absDiff = Math.abs(diff);

        double[] scale = positive ? WINNER_PERCENTS_POSITIVE : WINNER_PERCENTS_NEGATIVE;

        for (int i = 0; i < DIFF_BREAKPOINTS.length - 1; i++) {
            int lower = DIFF_BREAKPOINTS[i];
            int upper = DIFF_BREAKPOINTS[i + 1];
            if (absDiff == lower) {
                return scale[i];
            }
            if (absDiff > lower && absDiff < upper) {
                double ratio = (absDiff - lower) / (double) (upper - lower);
                return scale[i] + ratio * (scale[i + 1] - scale[i]);
            }
        }
        return scale[scale.length - 1];
    }

    private static int getCatchUpPoints(int mmrMinusCR) {
        boolean positive = mmrMinusCR >= 0;
        int absDiff = Math.abs(mmrMinusCR);

        int[] pointsArray = positive ? CATCHUP_POINTS_POSITIVE : CATCHUP_POINTS_NEGATIVE;

        for (int i = 0; i < CATCHUP_BREAKPOINTS.length - 1; i++) {
            int lower = CATCHUP_BREAKPOINTS[i];
            int upper = CATCHUP_BREAKPOINTS[i + 1];
            if (absDiff == lower) {
                return pointsArray[i];
            }
            if (absDiff > lower && absDiff < upper) {
                double ratio = (absDiff - lower) / (double)(upper - lower);
                double interpolated = pointsArray[i] + ratio * (pointsArray[i + 1] - pointsArray[i]);
                return (int) Math.round(interpolated);
            }
        }
        return pointsArray[pointsArray.length - 1];

    }
}
