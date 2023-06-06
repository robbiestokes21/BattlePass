package org.dstokesncstudio.battlepass.model;

public class PlayerBattlePass {

    private String playerUUID;

    //random stats on each player
    private int player_keys;
    private int bp_level;
    private  int requiredKeysAmount;

    private int isOP;

    public PlayerBattlePass(String playerUUID, int isOP, int player_keys, int requiredKeysAmount, int bp_level){
        this.playerUUID = playerUUID;
        this.isOP = isOP;
        this.player_keys = player_keys;
        this.requiredKeysAmount = requiredKeysAmount;
        this.bp_level = bp_level;
    }

    public String getPlayerUUID(){
        return playerUUID;
    }
    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public int getIsOP() {
        return isOP;
    }

    public void setIsOP(int isOP) {
        this.isOP = isOP;
    }

    public int getKeys(){
        return player_keys;
    }
    public void setKeys(int player_keys){
        this.player_keys = player_keys;
    }

    public int getLevel() {
        return bp_level;
    }

    public void setLevel(int bp_level) {
        this.bp_level = bp_level;
    }

    public int getRequiredKeysAmount() {
        return requiredKeysAmount;
    }

    public void setRequiredKeysAmount(int requiredKeysAmount) {
        this.requiredKeysAmount = requiredKeysAmount;
    }
}
