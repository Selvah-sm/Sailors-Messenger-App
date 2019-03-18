package com.sailors.messenger;

public class Utils {

    // Device User Name
    public static String User_Name = "";
    public static String Device_UID = "";

    public static SailorsLogChain getSailorsHashMapWithTeamTokenID(String teamToken) {
        /**** CREATING OR ACCESING THE HASHMAP OF A TEAM ****/
        if (InMemoryHashMaps.LOG_CHAIN_HASHMAP.get(teamToken) == null) {
            InMemoryHashMaps.LOG_CHAIN_HASHMAP.put(teamToken, new SailorsLogChain());
        }
        return InMemoryHashMaps.LOG_CHAIN_HASHMAP.get(teamToken);
        /*********/
    }
}
