package com.nonopichy.capeall.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtil {

    public static void enviarMensagemColorida(String mensagem) {
        if(Minecraft.getMinecraft().thePlayer!=null)
            Minecraft.getMinecraft().thePlayer.addChatMessage(formatarMensagem(mensagem));
    }

    private static ChatComponentText formatarMensagem(String mensagem) {
        ChatComponentText componente = new ChatComponentText("");

        String[] partes = mensagem.split("&");
        boolean primeiro = true;

        for (String parte : partes) {
            if (primeiro) {
                componente.appendText(parte);
                primeiro = false;
            } else {
                EnumChatFormatting cor = obterCorPorCodigo(parte.charAt(0));
                if (cor != null) {
                    componente.appendSibling(new ChatComponentText(parte.substring(1)).setChatStyle(new ChatStyle().setColor(cor)));
                } else {
                    componente.appendText("&" + parte);
                }
            }
        }

        return componente;
    }

    private static EnumChatFormatting obterCorPorCodigo(char codigo) {
        for (EnumChatFormatting cor : EnumChatFormatting.values()) {
            if (cor.toString().charAt(1) == codigo) {
                return cor;
            }
        }
        return null;
    }
}