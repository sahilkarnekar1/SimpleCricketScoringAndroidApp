package com.example.cricketscoringapp.Models;

public class Match {
    private String matchId;
    private String userId;
    private String team1Id;
    private String team2Id;
    private double overs;
    private String tossWinner;
    private String electedTo;
    private String tournamentId;


    public Match() {
    }

    public Match(String matchId, String userId, String team1Id, String team2Id, double overs, String tossWinner, String electedTo, String tournamentId) {
        this.matchId = matchId;
        this.userId = userId;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.overs = overs;
        this.tossWinner = tossWinner;
        this.electedTo = electedTo;
        this.tournamentId = tournamentId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(String team1Id) {
        this.team1Id = team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(String team2Id) {
        this.team2Id = team2Id;
    }

    public double getOvers() {
        return overs;
    }

    public void setOvers(double overs) {
        this.overs = overs;
    }

    public String getTossWinner() {
        return tossWinner;
    }

    public void setTossWinner(String tossWinner) {
        this.tossWinner = tossWinner;
    }

    public String getElectedTo() {
        return electedTo;
    }

    public void setElectedTo(String electedTo) {
        this.electedTo = electedTo;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }
}