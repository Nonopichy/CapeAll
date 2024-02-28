package com.nonopichy.capeall.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ChatUtil {

    public static void enviarMensagemColorida(String mensagem) {
        if(Minecraft.getMinecraft().player != null)
            Minecraft.getMinecraft().player.sendMessage(formatarMensagem(mensagem));
    }

    private static ITextComponent formatarMensagem(String mensagem) {
        TextComponentString componente = new TextComponentString("");

        String[] partes = mensagem.split("&");
        boolean primeiro = true;

        for (String parte : partes) {
            if (primeiro) {
                componente.appendText(parte);
                primeiro = false;
            } else {
                TextFormatting cor = obterCorPorCodigo(parte.charAt(0));
                if (cor != null) {
                    componente.appendSibling(new TextComponentString(parte.substring(1)).setStyle(new Style().setColor(cor)));
                } else {
                    componente.appendText("&" + parte);
                }
            }
        }

        return componente;
    }

    private static TextFormatting obterCorPorCodigo(char codigo) {
        for (TextFormatting cor : TextFormatting.values()) {
            if (cor.toString().charAt(1) == codigo) {
                return cor;
            }
        }
        return null;
    }
}
