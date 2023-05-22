package client.cryptoclient.controllers;

import client.cryptoclient.cryptoAlgorithms.Crypto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Builder
public class ClientCryptoProgress {
    private State state;
    private Crypto crypto;
}

