package com.rumor.tracing.blockchain.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumor.tracing.entity.Rumor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RumorContract {

    private final Contract contract;
    private final ObjectMapper objectMapper;

    public String recordRumor(Rumor rumor) throws Exception {
        try {
            String rumorJson = objectMapper.writeValueAsString(rumor);
            byte[] result = contract.submitTransaction("recordRumor", rumorJson);
            return new String(result, StandardCharsets.UTF_8);
        } catch (ContractException | TimeoutException e) {
            log.error("Error recording rumor to blockchain", e);
            throw new RuntimeException("Failed to record rumor to blockchain", e);
        }
    }

    public String verifyRumor(Long rumorId, String verificationResult) throws Exception {
        try {
            byte[] result = contract.submitTransaction("verifyRumor",
                String.valueOf(rumorId), verificationResult);
            return new String(result, StandardCharsets.UTF_8);
        } catch (ContractException | TimeoutException e) {
            log.error("Error verifying rumor on blockchain", e);
            throw new RuntimeException("Failed to verify rumor on blockchain", e);
        }
    }

    public String getRumorHistory(Long rumorId) throws Exception {
        try {
            byte[] result = contract.evaluateTransaction("getRumorHistory",
                String.valueOf(rumorId));
            return new String(result, StandardCharsets.UTF_8);
        } catch (ContractException e) {
            log.error("Error getting rumor history from blockchain", e);
            throw new RuntimeException("Failed to get rumor history from blockchain", e);
        }
    }
} 