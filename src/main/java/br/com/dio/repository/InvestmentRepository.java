package br.com.dio.repository;

import br.com.dio.exception.AccountWithInvestmentException;
import br.com.dio.exception.InvestmentNotFoundException;
import br.com.dio.exception.PixInUseException;
import br.com.dio.exception.WalletNotFoundException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.Investiment;
import br.com.dio.model.InvestmentWallet;

import java.util.ArrayList;
import java.util.List;

import static br.com.dio.repository.CommonsRepository.checkFundsForTransaction;

public class InvestmentRepository {

    private long nextId;

    private final List<Investiment> investiments = new ArrayList<>();
    private final List<InvestmentWallet> wallets = new ArrayList<>();

    public Investiment create(final long tax, final long initialFunds) {
        this.nextId++;
        var investment = new Investiment(this.nextId, tax, initialFunds);
        investiments.add(investment);
        return investment;
    }

    public InvestmentWallet initInvestment(final AccountWallet account, final long id) {
        var accountsInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();
        if (accountsInUse.contains(account)) {
            throw new AccountWithInvestmentException("A conta '" + account + "' já possui um investimento");
        }

        var investment = findById(id);
        checkFundsForTransaction(account, investment.initialFunds());
        var wallet = new InvestmentWallet(investment, account, investment.initialFunds());
        wallets.add(wallet);
        return wallet;

    }

    public InvestmentWallet deposit(final String pix, final long funds) {
        var wallet = findWalletByAccount(pix);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "Investimento");
        return wallet;
    }


    // saque da conta de investimento
    public InvestmentWallet withdraw(final String pix, final long funds) {
        var wallet = findWalletByAccount(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount().addMoney(wallet.reduceMoney(funds), wallet.getService(), "Saque de investimento");
        if (wallet.getFunds() == 0) {
            wallets.remove(wallet);
        }
        return wallet;
    }

    public void updateAmount() {
        wallets.forEach(w -> w.updateAmount(w.getInvestment().tax()));
    }

    public Investiment findById(final long id) {
        return investiments.stream()
                .filter(a -> a.id() == id)
                .findFirst()
                .orElseThrow(
                        () -> new InvestmentNotFoundException("O investimento '" + id + "' não foi encontrado")
                );
    }

    public InvestmentWallet findWalletByAccount(final String pix) {
        return wallets.stream()
                .filter(w -> w.getAccount().getPix().contains(pix))
                .findFirst()
                .orElseThrow(
                        () -> new WalletNotFoundException("Carteira não encontrada")
                );

    }

    public List<InvestmentWallet> listWallets() {
        return this.wallets;
    }

    public List<Investiment> list() {
        return this.investiments;
    }

}
