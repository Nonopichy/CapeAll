package com.nonopichy.capeall;

import java.util.Map;

/**
 * Everything the core needs from Minecraft. Each Minecraft version implements it once
 * (versions/&lt;mc&gt;/common), so the protocol, config and menu logic never touch Minecraft classes.
 */
public interface Platform {

    /** Name of the local player, or null when not in a world. */
    String localPlayerName();

    /** Name -> skin layer bits (7 bits) of every other loaded player. */
    Map<String, Integer> otherPlayersSkinLayers();

    /** Skin layer bits enabled in the local player's options. */
    int getSkinLayers();

    /** Enables exactly these skin layer bits and sends them to the server. */
    void setSkinLayers(int bits);

    /** Shows a chat message (with § color codes) to the local player. */
    void chat(String message);

    /** Name of the key that opens the menu (players can rebind it). */
    String menuKeyName();
}
