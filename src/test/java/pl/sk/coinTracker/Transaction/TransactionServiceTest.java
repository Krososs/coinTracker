package pl.sk.coinTracker.Transaction;

import org.junit.jupiter.api.Test;
import pl.sk.coinTracker.Wallet.Wallet;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        var mockTransactionRepository = mock(TransactionRepository.class);
        Transaction t = new Transaction();
        t.setId(1L);
        t.setDate(new Date());
        t.setNote("note1");
        when(mockTransactionRepository.save(t)).thenReturn(t);

        var testService = new TransactionService(mockTransactionRepository);
        Transaction newTransaction = testService.addNewTransaction(t);

        assertEquals(t.getId(), newTransaction.getId());
        assertEquals(t.getNote(),newTransaction.getNote());
        assertEquals(t.getDate(), newTransaction.getDate());
    }

    @Test
    void should_edit_transaction() {
        var transactionRepository = inMemoryTransactionRepository();

        Transaction t = new Transaction();
        t.setId(1L);
        t.setPrice(new BigDecimal(3.3));
        t.setAmount(new BigDecimal(10.0));
        t.setDate(new Date());
        t.setNote("test");


        Transaction toEDit = new Transaction();
        toEDit.setId(1L);
        toEDit.setPrice(new BigDecimal(5.5));
        toEDit.setAmount(new BigDecimal(15.0));
        toEDit.setNote("edited");
        toEDit.setDate(new Date());


        var testService = new TransactionService(transactionRepository);
        transactionRepository.save(t);
        testService.editTransaction(toEDit);

        Transaction editedTransaction = transactionRepository.findById(1L).get();

        assertEquals(editedTransaction.getPrice(),toEDit.getPrice());
        assertEquals(editedTransaction.getAmount(),toEDit.getAmount());
        assertEquals(editedTransaction.getNote(),toEDit.getNote());
        assertEquals(editedTransaction.getDate(), toEDit.getDate());

    }

    @Test
    void should_delete_transaction() {
        var transactionRepository = inMemoryTransactionRepository();
        Transaction t = new Transaction();
        t.setId(1L);

        var testService = new TransactionService(transactionRepository);
        transactionRepository.save(t);
        testService.deleteTransactionById(1L);

        assertFalse(transactionRepository.findById(1L).isPresent());
    }

    @Test
    void should_delete_transaction_by_coin_id() {
        var transactionRepository = inMemoryTransactionRepository();
        Wallet w1 = new Wallet();
        w1.setId(1L);

        Wallet w2 = new Wallet();
        w2.setId(2L);

        Transaction t = new Transaction();
        t.setId(1L);
        t.setCoinId(1L);
        t.setWallet(w1);

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setCoinId(1l);
        t2.setWallet(w1);

        Transaction t3 = new Transaction();
        t3.setId(3L);
        t3.setCoinId(1l);
        t3.setWallet(w2);

        var testService = new TransactionService(transactionRepository);
        transactionRepository.save(t);
        transactionRepository.save(t2);
        transactionRepository.save(t3);

        testService.deleteTransactionsByCoinId(1L,1l);

        assertEquals(transactionRepository.findByWallet(w1).size(),0);
        assertEquals(transactionRepository.findByWallet(w2).size(),1);
    }

    @Test
    void test_get_transaction_by_wallet() {
        var transactionRepository = inMemoryTransactionRepository();
        Wallet w1 = new Wallet();
        w1.setId(1L);

        Wallet w2 = new Wallet();
        w2.setId(2L);

        Transaction t = new Transaction();
        t.setId(1L);
        t.setCoinId(1L);
        t.setWallet(w1);

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setCoinId(1l);
        t2.setWallet(w1);

        Transaction t3 = new Transaction();
        t3.setId(3L);
        t3.setCoinId(1l);
        t3.setWallet(w2);

        var testService = new TransactionService(transactionRepository);
        transactionRepository.save(t);
        transactionRepository.save(t2);
        transactionRepository.save(t3);

        assertEquals(transactionRepository.findByWallet(w1).size(),2);
        assertEquals(transactionRepository.findByWallet(w2).size(),1);
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
                for(Iterator<Map.Entry<Long, Transaction>> it = transactions.entrySet().iterator(); it.hasNext();){
                    Map.Entry<Long, Transaction> entry = it.next();
                    if(entry.getValue().getWallet().getId().equals(walletId) && entry.getValue().getCoinId().equals(coinId))
                        it.remove();
                }
            }

            @Override
            public List<Transaction> findByWallet(Wallet w) {
                return transactions.values().stream()
                        .filter(t -> t.getWallet().getId().equals(w.getId()))
                        .collect(Collectors.toList());
            }

            @Override
            public Optional<Transaction> findById(Long id) {
                return Optional.ofNullable(transactions.get(id));
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
            public void deleteById(Long id) {transactions.remove(id);
            }
        };
    }

}