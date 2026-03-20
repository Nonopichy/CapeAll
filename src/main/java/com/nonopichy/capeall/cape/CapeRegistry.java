package com.nonopichy.capeall.cape;

import java.util.HashMap;
import java.util.Map;

public class CapeRegistry {

    private static final Map<Integer, String> ID_TO_NAME = new HashMap<>();
    private static final Map<String, Integer> NAME_TO_ID = new HashMap<>();
    private static String[] allNames;

    static {
        register(0, "15");
        register(1, "1cake");
        register(2, "2011");
        register(3, "2012");
        register(4, "2013");
        register(5, "2015");
        register(6, "2016");
        register(7, "360");
        register(8, "awesom");
        register(9, "bday");
        register(10, "blonk");
        register(11, "bun1");
        register(12, "bun2");
        register(13, "bun3");
        register(14, "cherry");
        register(15, "chitrans");
        register(16, "classic");
        register(17, "cobalt");
        register(18, "customer");
        register(19, "db");
        register(20, "dev00");
        register(21, "founde");
        register(22, "hp");
        register(23, "japtrans");
        register(24, "migrator");
        register(25, "mojang");
        register(26, "mojira");
        register(27, "noc");
        register(28, "nyan");
        register(29, "oalblack");
        register(30, "oblack");
        register(31, "oblue");
        register(32, "ocyan");
        register(33, "ogray");
        register(34, "ogreen");
        register(35, "opurple");
        register(36, "ored");
        register(37, "ostand");
        register(38, "owhite");
        register(39, "oyellow");
        register(40, "ppride");
        register(41, "prisma");
        register(42, "realms");
        register(43, "scrolls");
        register(44, "snowman");
        register(45, "spade");
        register(46, "squid");
        register(47, "studios");
        register(48, "tc2010");
        register(49, "test");
        register(50, "tik");
        register(51, "tpan");
        register(52, "trans");
        register(53, "turtle");
        register(54, "ty2011");
        register(55, "un1");
        register(56, "un2");
        register(57, "un3");
        register(58, "valentin");
        register(59, "vanilla");
        register(60, "vete");

        allNames = new String[ID_TO_NAME.size()];
        for (int i = 0; i < allNames.length; i++) {
            allNames[i] = ID_TO_NAME.get(i);
        }
    }

    private static void register(int id, String name) {
        ID_TO_NAME.put(id, name);
        NAME_TO_ID.put(name, id);
    }

    public static String getNameById(int id) {
        return ID_TO_NAME.get(id);
    }

    public static int getIdByName(String name) {
        Integer id = NAME_TO_ID.get(name);
        return id != null ? id : -1;
    }

    public static boolean isValidId(int id) {
        return ID_TO_NAME.containsKey(id);
    }

    public static boolean isValidName(String name) {
        return NAME_TO_ID.containsKey(name);
    }

    public static int getCapeCount() {
        return ID_TO_NAME.size();
    }

    public static String[] getAllCapeNames() {
        return allNames;
    }
}
