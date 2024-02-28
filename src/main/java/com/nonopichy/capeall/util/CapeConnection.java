package com.nonopichy.capeall.util;

import com.nonopichy.capeall.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CapeConnection {

    private static final Map<String, String> playerData = new HashMap<>();

    public static boolean containsInPlayerData(String playerName){
        return playerData.containsKey(removerCaracteresEspeciais(playerName));
    }

    public static String playersNamesToString() {
        if(Minecraft.getMinecraft().getConnection()==null){
            return "[\"nada\"]";
        }
        Collection<NetworkPlayerInfo> collection = Minecraft.getMinecraft().getConnection().getPlayerInfoMap();
        StringBuilder stringBuilder = new StringBuilder("[");
        int playerCount = 0;
        for (NetworkPlayerInfo networkPlayerInfo : collection) {
            String playerName = removerCaracteresEspeciais(networkPlayerInfo.getGameProfile().getName());
            stringBuilder.append("\"").append(playerName).append("\"");
            if (++playerCount < collection.size()) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static void updatePlayerData() {
        new Thread(() -> {
            try {
                JSONObject responseJson = sendJsonPostRequest(ModConfig.serverURL, new JSONObject("{\"players\": " + playersNamesToString() + "}"));
                atualizarMapaPlayerData(responseJson);
                ChatUtil.enviarMensagemColorida("&a[CapeAll] Capas atualizadas com sucesso!");
            } catch (Exception e) {
                e.printStackTrace();
                ChatUtil.enviarMensagemColorida("&c[CapeAll] Ocorreu um erro.");
            }
        }).start();
    }

    private static void atualizarMapaPlayerData(JSONObject responseJson) {
        for (String playerName : responseJson.keySet()) {
            String playerValue = responseJson.getString(playerName);
            playerData.put(playerName, playerValue);
        }
    }

    public static String removerCaracteresEspeciais(String input) {
        return input.replaceAll("[^a-zA-Z0-9_]", "");
    }
    public static String getCapeAll(String playernick) {
        return playerData.getOrDefault(playernick, "default");
    }

    private static JSONObject sendJsonPostRequest(String url, JSONObject requestBody) throws Exception {
        SSLContextBuilder builder = SSLContexts.custom();
        builder.loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) {
                return true;
            }
        });
        SSLContext sslContext = builder.build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext, new X509HostnameVerifier() {
            @Override
            public void verify(String host, SSLSocket ssl)
                    throws IOException {
            }

            @Override
            public void verify(String host, X509Certificate cert)
                    throws SSLException {
            }

            @Override
            public void verify(String host, String[] cns,
                               String[] subjectAlts) throws SSLException {
            }

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("https", sslsf)
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm).build();

        HttpPost httpPost = new HttpPost(url);

        StringEntity requestEntity = new StringEntity(requestBody.toString());
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(requestEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))) {
            StringBuilder responseText = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseText.append(line);
            }

            return new JSONObject(responseText.toString());
        }

    }

}
