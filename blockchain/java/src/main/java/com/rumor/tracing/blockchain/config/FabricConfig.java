package com.rumor.tracing.blockchain.config;

import org.hyperledger.fabric.gateway.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Profile("!test")
public class FabricConfig {

    @Value("${blockchain.network-config-path}")
    private String networkConfigPath;

    @Value("${blockchain.wallet-path}")
    private String walletPath;

    @Value("${blockchain.user-name}")
    private String userName;

    @Value("${blockchain.channel-name}")
    private String channelName;

    @Value("${blockchain.contract-name}")
    private String contractName;

    @Bean
    public Wallet getWallet() throws IOException {
        return Wallets.newFileSystemWallet(Paths.get(walletPath));
    }

    @Bean
    public Gateway gateway() throws Exception {
        Path networkConfigFile = Paths.get(networkConfigPath);
        Gateway.Builder builder = Gateway.createBuilder()
                .identity(getWallet(), userName)
                .networkConfig(networkConfigFile);
        return builder.connect();
    }

    @Bean
    public Network network(Gateway gateway) {
        return gateway.getNetwork(channelName);
    }

    @Bean
    public Contract contract(Network network) {
        return network.getContract(contractName);
    }
} 