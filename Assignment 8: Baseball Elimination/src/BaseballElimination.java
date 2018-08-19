import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

/**
 *  BaseballElimination class datatype
 *  Given the standings in a sports division at some point during the season,
 *  finds which teams have been mathematically eliminated from winning their division
 *  and subset R of teams that eliminates given team by solving a maxflow/mincut problem.
 *  All calculation are performed in the constructor in O(T^4) in the worst case and requires
 *  O(T^2) extra memory, where T is the number of teams. In practice, the algorithm will run much faster.
 *
 *  For example, on real tests:
 *      n   constructor
 *  ---------------------------
 *    30       0.57
 *    36       0.79
 *    42       1.42
 *    48       2.79
 *    54       4.54
 *    60       8.38
 *  time = 6.82e-07 * n^3.94
 *
 *  Afterwards, isEliminated and certificateOfElimination methods (and all others) works in O(1)
 *
 */
public class BaseballElimination {

    // Division description
    private final ArrayList<String> idList; // idList[team_id] == team_name
    private final int teamCount;            // number of teams in division
    private final int[] wins;               // wins[team_id] == number of wins
    private final int[] losses;             // losses[team_id] == number of losses
    private final int[] remaining;          // remaining[team_id] == number of remaining games
    private final int[][] games;            // games[team_1_id][team_2_id] == number of games left
                                            // to play against team_2 for team_1

    // Certificate of elimination
    private final ArrayList<Bag<String>> subsetR; // RSubsets[team_id] == Bag of team names that eliminates team_id

    // Auxilary indexes for eliminationNetwork construction
    private final int sInd;                             // 0
    private final int gameVerticesBeg;                  // sInd + 1
    private final int gameVerticesEnd;                  // gameVerticesBeg + teamCount * (teamCount - 1) / 2;
    private final int teamVerticesBeg;                  // gameVerticesEnd + 1
    private final int teamVerticesEnd;                  // teamVerticesBeg + teamCount
    private final int tInd;                             // teamVerticesEnd + 1
    private final int eliminationNetworkVerticesCount;  // tInd + 1

    /**
     * Create a baseball division from given filename in format specified below
     * @param filename of baseball division discription
     */
    public BaseballElimination(String filename) {
        In divisionDescription = new In(filename);

        String line = divisionDescription.readLine();
        teamCount = Integer.parseInt(line);
        wins = new int[teamCount];
        losses = new int[teamCount];
        remaining = new int[teamCount];
        games = new int[teamCount][teamCount];

        idList = new ArrayList<String>();

        int teamNameInd = 0;
        int winsInd = teamNameInd + 1;
        int lossesInd = winsInd + 1;
        int remainingInd = lossesInd + 1;
        int gamesInd = remainingInd + 1;

        for (int i = 0; i < teamCount; i++) {
            line = divisionDescription.readLine();
            line = line.trim().replaceAll(" +", " ");
            String[] divisionLine = line.split(" ");
            idList.add(divisionLine[teamNameInd]);
            wins[i] = Integer.parseInt(divisionLine[winsInd]);
            losses[i] = Integer.parseInt(divisionLine[lossesInd]);
            remaining[i] = Integer.parseInt(divisionLine[remainingInd]);
            for (int j = 0; j < teamCount; j++)
                games[i][j] = Integer.parseInt(divisionLine[gamesInd + j]);
        }

        sInd = 0;
        gameVerticesBeg = sInd + 1;
        gameVerticesEnd = gameVerticesBeg + teamCount * (teamCount - 1) / 2;
        teamVerticesBeg = gameVerticesEnd;
        teamVerticesEnd = teamVerticesBeg + teamCount;
        tInd = teamVerticesEnd;
        eliminationNetworkVerticesCount = tInd + 1;

        subsetR = new ArrayList<Bag<String>>();
        for (int i = 0; i < teamCount; i++) 
            subsetR.add(calculateRSubset(i));
    }

    /**
     * @param teamId
     * @return R subset of teams that eliminates given team by solving a maxflow/mincut problem.
     */
    private Bag<String> calculateRSubset(int teamId) {
        Bag<String> teamIdSubsetR = new Bag<String>();
        boolean trivialElimination = false;
        for (int id = 0; id < teamCount; id++) {
            if (id == teamId)
                continue;
            if (wins[teamId] + remaining[teamId] < wins[id]) {
                // trivial elimination
                teamIdSubsetR.add(idList.get(id));
                trivialElimination = true;
                break;
            }
        }
        if (!trivialElimination) {
            FlowNetwork eliminationNetwork = new FlowNetwork(eliminationNetworkVerticesCount);
            int vertexId = gameVerticesBeg;
            for (int i = 0; i < teamCount; i++) {
                if (i == teamId) {
                    vertexId += teamCount - (i + 1);
                    continue;
                }
                for (int j = i + 1; j < teamCount; j++, vertexId++) {
                    if (j == teamId) {
                        continue;
                    }
                    // Add edges from s the game vertices:
                    // where e_game = (s, i-j) with capacity g_ij,
                    eliminationNetwork.addEdge(new FlowEdge(sInd, vertexId, games[i][j]));
                    // Add edges from s the team vertices:
                    // where e_team_i = (i-j, i), e_team_j = (i-j, j) with capacity INF
                    eliminationNetwork.addEdge(new FlowEdge(vertexId, teamVerticesBeg + i, Integer.MAX_VALUE));
                    eliminationNetwork.addEdge(new FlowEdge(vertexId, teamVerticesBeg + j, Integer.MAX_VALUE));
                }
                // Add edges from team vertices to t:
                // where e = (i, t) with capacity wins[teamId] + remaining[teamId] - wins[i]
                eliminationNetwork.addEdge(new FlowEdge(teamVerticesBeg + i, tInd,
                        wins[teamId] + remaining[teamId] - wins[i]));
            }
            assert vertexId == gameVerticesEnd;

            // Compute Max Flow and Min Cut in eliminationNetwork
            FordFulkerson eliminationSolver = new FordFulkerson(eliminationNetwork, sInd, tInd);

            // Find teams in Min Cut
            for (int v = teamVerticesBeg; v < teamVerticesEnd; v++)
                if (eliminationSolver.inCut(v))
                    teamIdSubsetR.add(idList.get(v - teamVerticesBeg));
        }
        return teamIdSubsetR.size() > 0 ? teamIdSubsetR : null;
    }
    
    /**
     * @return number of teams in division
     */
    public int numberOfTeams() {
        return teamCount;
    }

    /**
     * @return all team names in division
     */
    public Iterable<String> teams() {
        return idList;
    }

    /**
     * @param team
     * @throws an IllegalArgumentException unless {team is contained in division desctiption}
     */
    private void validateTeam(String team) {
        if (!idList.contains(team))
            throw new java.lang.IllegalArgumentException();
    }

    /**
     * @param team
     * @return number of wins for given team
     * @throws an IllegalArgumentException unless {team is contained in division desctiption}
     */
    public int wins(String team) {
        validateTeam(team);
        return wins[idList.indexOf(team)];
    }

    /**
     * @param team
     * @return number of losses for given team
     * @throws an IllegalArgumentException unless {team is contained in division desctiption}
     */
    public int losses(String team) {
        validateTeam(team);
        return losses[idList.indexOf(team)];
    }

    /**
     * @param team
     * @return number of remaining games for given team
     * @throws an IllegalArgumentException unless {team is contained in division desctiption}
     */
    public int remaining(String team) {
        validateTeam(team);
        return remaining[idList.indexOf(team)];
    }

    /**
     * @param team1
     * @param team2
     * @return number of remaining games between team1 and team2
     * @throws an IllegalArgumentException unless {teams are contained in division desctiption}
     */
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return games[idList.indexOf(team1)][idList.indexOf(team2)];
    }

    /**
     * @param team
     * @return is given team eliminated?
     * @throws an IllegalArgumentException unless {team is contained in division desctiption}
     */
    public boolean isEliminated(String team) {
        validateTeam(team);
        return subsetR.get(idList.indexOf(team)) != null;
    }

    /**
     * @param team
     * @return subset R of teams that eliminates given team; null if not eliminated
     * @throws an IllegalArgumentException unless {team is contained in division desctiption}
     */
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        return subsetR.get(idList.indexOf(team));
    }

    /**
     * Unit testing of this class
     */
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
