package com.rumor.tracing.blockchain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumor.tracing.blockchain.contract.RumorContract;
import com.rumor.tracing.blockchain.dto.RumorHistoryDTO;
import com.rumor.tracing.entity.Rumor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainService {

    private final RumorContract rumorContract;
    private final ObjectMapper objectMapper;

    public String recordRumorToBlockchain(Rumor rumor) {
        try {
            return rumorContract.recordRumor(rumor);
        } catch (Exception e) {
            log.error("Failed to record rumor to blockchain", e);
            throw new RuntimeException("Blockchain operation failed", e);
        }
    }

    public String verifyRumorOnBlockchain(Long rumorId, String verificationResult) {
        try {
            return rumorContract.verifyRumor(rumorId, verificationResult);
        } catch (Exception e) {
            log.error("Failed to verify rumor on blockchain", e);
            throw new RuntimeException("Blockchain operation failed", e);
        }
    }

    public List<RumorHistoryDTO> getRumorHistory(Long rumorId) {
        try {
            String historyJson = rumorContract.getRumorHistory(rumorId);
            return objectMapper.readValue(historyJson, 
                new TypeReference<List<RumorHistoryDTO>>() {});
        } catch (Exception e) {
            log.error("Failed to get rumor history from blockchain", e);
            throw new RuntimeException("Blockchain operation failed", e);
        }
    }
} 