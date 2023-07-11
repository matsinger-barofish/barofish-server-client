package com.matsinger.barofishserver;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/github_webhook")
public class GithubWebhook {
    @Value("${github.webhook.secret}")
    private String githubWebhookSecret;
    @Value("${github.webhook.branch}")
    private String githubBranch;

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String HMAC_SHA256_encode(String key, String message) throws NoSuchAlgorithmException, InvalidKeyException {
        System.out.println(key);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(message.getBytes());

        return Hex.encodeHexString(rawHmac);
    }


    @PostMapping(value = "")
    ResponseEntity<Boolean> githubWebhookCallback(@RequestHeader(value = "x-hub-signature-256", required = false) String signature,
                                                  @RequestBody String payload)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        if (githubBranch.equals("none")) return ResponseEntity.status(400).body(false);
        System.out.println("Github Webhook Request received");
        JSONObject jsonObject = new JSONObject(payload);
        String hash = String.format("sha256=%s", HmacUtils.hmacSha256Hex(githubWebhookSecret, payload));
        if (!hash.equals(signature)) {
            return ResponseEntity.status(400).body(false);
        }
        if (jsonObject.get("ref").equals("refs/heads/" + githubBranch)) {
            System.out.println("running hook_github.sh");
            Process process = Runtime.getRuntime().exec("./hook_github.sh");
            // Read output
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }
        return ResponseEntity.ok(true);
    }
}
