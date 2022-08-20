package pl.sk.coinTracker.Transaction;

import org.junit.jupiter.api.Test;
import pl.sk.coinTracker.Wallet.Wallet;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    @Test
    void check_if_wallet_contains_coin_and_return_true() {
        var transactionRepository = inMemoryTransactionRepository();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        Transaction t = new Transaction(1L,"BUY",wallet,BigDecimal.ZERO,BigDecimal.ZERO, new Date(),"note");
        transactionRepository.save(t);

        var testService = new TransactionService(transactionRepository);

        assertTrue(testService.walletContainsCoin(1L,1L));
    }

    @Test
    void check_if_wallet_contains_coin_and_return_false() {
        var transactionRepository = inMemoryTransactionRepository();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        Transaction t = new Transaction(1L,"BUY",wallet,BigDecimal.ZERO,BigDecimal.ZERO, new Date(),"note");
        transactionRepository.save(t);

        var testService = new TransactionService(transactionRepository);

        assertFalse(testService.walletContainsCoin(1L,2L));
        assertFalse(testService.walletContainsCoin(2L,1L));
    }

    @Test
    void should_return_initial_transaction() {
        var transactionRepository = inMemoryTransactionRepository();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        Transaction t = new Transaction(1L,"BUY",wallet,BigDecimal.ZERO,BigDecimal.ZERO, new Date(),"initial");
        t.setId(1L);
        Transaction t2 = new Transaction(2L,"SELL", wallet, BigDecimal.TEN, BigDecimal.ZERO, new Date(), "note2");
        t2.setId(2L);
        transactionRepository.save(t);
        transactionRepository.save(t2);

        var testService = new TransactionService(transactionRepository);
        Transaction givenTransaction = testService.getInitialTransaction(1L,1L);

        assertEquals(t.getCoinId(), givenTransaction.getCoinId());
        assertEquals(t.getAmount(), givenTransaction.getAmount());
        assertEquals(t.getPrice(), givenTransaction.getPrice());
        assertEquals(t.getNote(),"initial");
    }

    @Test
    void test_add_new_transaction() {
    }

    @Test
    void test_edit_transaction() {

    }

    @Test
    void test_delete_transaction() {

    }

    @Test
    void test_delete_transaction_by_coin_id() {

    }

    @Test
    void test_get_transaction_by_wallet() {

    }

    @Test
    void test_get_transaction() {

    }

    private TransactionRepository inMemoryTransactionRepository() {
        Map<Long, Transaction> transactions = new HashMap<>();
        return new TransactionRepository() {
            @Override
            public Optional<Transaction> getInitialTransaction(Long walletId, Long coinId) {
                Optional<Transaction> result = transactions.values().stream()
                        .filter(t -> t.getWallet().getId().equals(walletId) &&
                                t.getCoinId().equals(coinId) &&
                                t.getAmount().equals(BigDecimal.ZERO) &&
                                t.getPrice().equals(BigDecimal.ZERO)
                        )
                        .findFirst();
                return result.isPresent() ? result : Optional.empty();
            }

            @Override
            public List<Transaction> getTransactions(Long walletId, Long coinId) {
                return null;
            }

            @Override
            public void deleteByCoinId(Long walletId, Long coinId) {

            }

            @Override
            public List<Transaction> findByWallet(Wallet w) {
                return null;
            }

            @Override
            public Optional<Transaction> findById(Long id) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long id) {
                return false;
            }

            @Override
            public Transaction save(Transaction coin) {
                return transactions.put(coin.getId(),coin);
            }

            @Override
            public void deleteById(Long id) {

            }
        };
    }

}