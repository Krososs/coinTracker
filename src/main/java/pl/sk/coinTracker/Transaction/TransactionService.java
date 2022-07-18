package pl.sk.coinTracker.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public boolean transactionExists(Long id) {
        return transactionRepository.existsById(id);
    }

    public boolean walletContainsCoin(Long walletId, Long coinId) {
        return transactionRepository.getInitialTransaction(walletId, coinId).isPresent();
    }

    public Transaction getInitialTransaction(Long walletId, Long coinId) {
        return transactionRepository.getInitialTransaction(walletId, coinId).get();
    }

    public void addNewTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public void editTransaction(Transaction transaction) {

        Transaction t = transactionRepository.getById(transaction.getId());

        t.setPrice(transaction.getPrice());
        t.setAmount(transaction.getAmount());
        t.setDate(transaction.getDate());
        t.setNote(transaction.getNote());

        transactionRepository.save(t);
    }

    public void deleteTransactionById(Long id) {
        transactionRepository.deleteById(id);
    }

    public void deleteTransactionsByWalletId(Long id) {
        transactionRepository.deleteByWalletId(id);
    }

    public void deleteTransactionsByCoinId(Long walletId, Long coinId) {
        transactionRepository.deleteByCoinId(walletId, coinId);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.getById(id);
    }

    public List<Transaction> getTransactionsByWalletId(Long walletId) {
        return transactionRepository.findBywalletId(walletId);
    }

    public List<Transaction> getTransactions(Long walletId, Long coinId) {
        return transactionRepository.getTransactions(walletId, coinId);
    }
}
