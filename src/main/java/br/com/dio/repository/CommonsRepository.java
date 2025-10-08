package br.com.dio.repository;

import br.com.dio.exception.NoFundsEnoughtException;
import br.com.dio.exception.WalletNotFoundException;
import br.com.dio.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonsRepository {

    public static void checkFundsForTransaction(final Wallet source, final long amount) {
        if (source.getFunds() < amount) {
            throw new NoFundsEnoughtException("Sua conta não possui fundos o suficientes para realizar esta operação");
        }
    }

    public static List<Money> generateMoney(final UUID transactionId, final long funds, final String description) {
        var history = new MoneyAudit(transactionId, BankService.ACCOUNT, description, OffsetDateTime.now());
        return Stream.generate(() -> new Money(history)).limit(funds).toList();
    }
}
