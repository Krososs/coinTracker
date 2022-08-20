package pl.sk.coinTracker.Transaction;
import org.springframework.stereotype.Service;
import pl.sk.coinTracker.Wallet.Wallet;

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
    public List<Transaction> findByWallet(Wallet w){
        return transactionRepository.findByWallet(w);
    }

    public void addNewTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public void editTransaction(Transaction transaction) {

        Transaction t = transactionRepository.findById(transaction.getId()).get();

        t.setPrice(transaction.getPrice());
        t.setAmount(transaction.getAmount());
        t.setDate(transaction.getDate());
        t.setNote(transaction.getNote());

        transactionRepository.save(t);
    }

    public void deleteTransactionById(Long id) {
        transactionRepository.deleteById(id);
    }

    public void deleteTransactionsByCoinId(Long walletId, Long coinId) {
        transactionRepository.deleteByCoinId(walletId, coinId);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).get();
    }
    public List<Transaction> getTransactionsByWallet(Wallet w){
        return transactionRepository.findByWallet(w);
    }

    public List<Transaction> getTransactions(Long walletId, Long coinId) {
        return transactionRepository.getTransactions(walletId, coinId);
    }

}
