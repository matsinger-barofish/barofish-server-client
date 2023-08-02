package com.matsinger.barofishserver;

import com.google.gson.JsonObject;
import com.matsinger.barofishserver.utils.ShRunner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/github_webhook")
public class GithubWebhook {
    @Value("${github.webhook.secret}")
    private String githubWebhookSecret;
    @Value("${github.webhook.branch}")
    private String githubBranch;

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
            ShRunner shRunner = new ShRunner();
            String cmds = "sh ~/server-update.sh";
            String[] callCmd = {"/bin/bash", "-c", cmds};
            Map map = shRunner.execCommand(callCmd);
            System.out.println(map);
        }
        return ResponseEntity.ok(true);
    }
}
