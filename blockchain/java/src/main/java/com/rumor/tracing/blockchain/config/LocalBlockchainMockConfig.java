package com.rumor.tracing.blockchain.config;
import static org.mockito.Mockito.*;

import com.rumor.tracing.blockchain.contract.RumorContract;
import com.rumor.tracing.blockchain.service.BlockchainService;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalBlockchainMockConfig {

    @Bean
    public Gateway gateway() {
        return mock(Gateway.class);
    }

    @Bean
    public Network network(Gateway gateway) {
        return mock(Network.class);
    }

    @Bean
    public Contract contract(Network network) {
        return mock(Contract.class);
    }

    @Bean
    public RumorContract rumorContract() {
        return mock(RumorContract.class);
    }

    @Bean
    public BlockchainService blockchainService() {
        return mock(BlockchainService.class);
    }
} 