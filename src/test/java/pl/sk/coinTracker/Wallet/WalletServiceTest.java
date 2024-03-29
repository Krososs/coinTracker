package pl.sk.coinTracker.Wallet;

import org.junit.jupiter.api.Test;
import pl.sk.coinTracker.Transaction.Transaction;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Test
    void test_created_wallet() {
        var mockWalletRepository = mock(WalletRepository.class);
        Wallet w = new Wallet();
        w.setName("wallet");
        w.setType(WalletType.ON_CHAIN);
        when(mockWalletRepository.save(w)).thenReturn(w);

        var testService = new WalletService(mockWalletRepository);
        Wallet createdWallet = testService.createNewWallet(w, 2L);

        assertEquals(w.getOwnerId(), createdWallet.getOwnerId());
        assertEquals(w.getName(), createdWallet.getName());
        assertEquals(w.getAth(), createdWallet.getAth());
        assertEquals(w.getType(), createdWallet.getType());
    }

    @Test
    void should_rename_wallet() {
        var walletRepository = inMemoryWalletRepository();
        Wallet wallet = new Wallet();
        wallet.setName("wallet");
        wallet.setId(1L);

        var testService = new WalletService(walletRepository);
        walletRepository.save(wallet);
        testService.editWallet("renamedWallet", 1L);

        assertEquals("renamedWallet", walletRepository.findById(1L).get().getName());
    }

    @Test
    void should_delete_wallet() {
        var walletRepository = inMemoryWalletRepository();
        Wallet wallet = new Wallet();
        wallet.setId(1L);

        var testService = new WalletService(walletRepository);
        walletRepository.save(wallet);
        testService.deleteWallet(1L);

        assertFalse(walletRepository.findById(1L).isPresent());
    }

    @Test
    void check_if_wallet_name_already_exist_and_return_true() {
        var walletRepository = inMemoryWalletRepository();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setOwnerId(1L);
        wallet.setName("wallet");

        var testService = new WalletService(walletRepository);
        walletRepository.save(wallet);

        assertTrue(testService.walletNameAlreadyExists("wallet", 1L));
    }

    @Test
    void check_if_user_is_wallet_owner_and_return_true() {
        var walletRepository = inMemoryWalletRepository();

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setOwnerId(1L);

        var testService = new WalletService(walletRepository);
        walletRepository.save(wallet);

        assertTrue(testService.userIsOwner(1L, 1L));
    }

    @Test
    void should_return_correct_percentange_change() {
        var testService = new WalletService(null);
        assertEquals(new BigDecimal("100.00"), testService.getPercentageChange(new BigDecimal("1.0"), new BigDecimal("2.0")));
        assertEquals(new BigDecimal("-50.00"), testService.getPercentageChange(new BigDecimal("2"), new BigDecimal("1")));
        assertEquals(new BigDecimal("33.33"), testService.getPercentageChange(new BigDecimal("3"), new BigDecimal("4")));
        assertEquals(new BigDecimal("15.38"), testService.getPercentageChange(new BigDecimal("13"), new BigDecimal("15")));
        assertEquals(new BigDecimal("50.00"), testService.getPercentageChange(new BigDecimal("4.4"), new BigDecimal("6.6")));
        assertEquals(new BigDecimal("0.00"), testService.getPercentageChange(new BigDecimal("1"), new BigDecimal("1")));
        assertEquals(new BigDecimal("-100.00"), testService.getPercentageChange(new BigDecimal("1"), new BigDecimal("0")));
    }

    @Test
    void should_return_zero() {
        var testService = new WalletService(null);
        assertEquals(new BigDecimal("0.00"), testService.getPercentageChange(new BigDecimal("0"), new BigDecimal("1")));
        assertEquals(new BigDecimal("0.00"), testService.getPercentageChange(new BigDecimal("0"), new BigDecimal("0")));
    }

    @Test
    void should_count_coins_amount() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);

        List<Transaction> transactions = List.of(
                new Transaction(1L, "BUY", wallet, new BigDecimal(3), new BigDecimal(1.1), new Date(), "note"),
                new Transaction(1L, "BUY", wallet, new BigDecimal(2), new BigDecimal(1.1), new Date(), "note"),
                new Transaction(1L, "SELL", wallet, new BigDecimal(1), new BigDecimal(1.1), new Date(), "note"),
                new Transaction(2L, "BUY", wallet, new BigDecimal(1), new BigDecimal(1.1), new Date(), "note"),
                new Transaction(2L, "SELL", wallet, new BigDecimal(1), new BigDecimal(1.1), new Date(), "note")
        );

        var testService = new WalletService(null);

        Map<Long, BigDecimal> coinsAmount = testService.countCoinsAmount(transactions);
        assertEquals(2, coinsAmount.size());
        assertEquals(new BigDecimal(4), coinsAmount.get(1L));
        assertEquals(new BigDecimal(0), coinsAmount.get(2L));
    }

    @Test
    void should_count_total_spend() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);

        List<Transaction> transactions = List.of(
                new Transaction(1L, "BUY", wallet, new BigDecimal(3), new BigDecimal(1.1), new Date(), "note"),
                new Transaction(1L, "SELL", wallet, new BigDecimal(2), new BigDecimal(1.1), new Date(), "note"),
                new Transaction(2L, "BUY", wallet, new BigDecimal(1), new BigDecimal(1.1), new Date(), "note"),
                new Transaction(2L, "SELL", wallet, new BigDecimal(1), new BigDecimal(1.1), new Date(), "note")
        );

        var testService = new WalletService(null);
        BigDecimal totalSpend = testService.getTotalSpend(transactions);
        assertEquals(new BigDecimal(1.1), totalSpend);
    }

    @Test
    void check_if_wallet_exist_and_return_true() {
        var walletRepository = inMemoryWalletRepository();

        Wallet wallet = new Wallet();
        wallet.setId(1L);

        var testService = new WalletService(walletRepository);
        walletRepository.save(wallet);

        assertTrue(testService.walletExists(1L));
    }

    @Test
    void check_if_wallet_exist_and_return_false() {
        var walletRepository = inMemoryWalletRepository();
        var testService = new WalletService(walletRepository);

        assertFalse(testService.walletExists(5L));
    }

    @Test
    void should_return_user_wallets_list() {
        var walletRepository = inMemoryWalletRepository();

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setOwnerId(1L);

        Wallet wallet2 = new Wallet();
        wallet2.setId(2L);
        wallet2.setName("wallet2");
        wallet2.setOwnerId(1L);

        var testService = new WalletService(walletRepository);
        walletRepository.save(wallet);
        walletRepository.save(wallet2);

        assertEquals(2, testService.getUserWallets(1L).size());
        assertEquals("wallet2", testService.getUserWallets(1L).stream().filter(w -> w.getId().equals(2L)).findFirst().get().getName());
    }

    @Test
    void should_return_wallet_with_given_id() {
        var walletRepository = inMemoryWalletRepository();

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setName("wallet");
        wallet.setOwnerId(1L);

        var testService = new WalletService(walletRepository);
        walletRepository.save(wallet);

        assertEquals("wallet", testService.getWalletById(1L).getName());
        assertEquals(1L, testService.getWalletById(1L).getOwnerId());
    }

    private WalletRepository inMemoryWalletRepository() {
        Map<Long, Wallet> wallets = new HashMap<>();
        return new WalletRepository() {
            @Override
            public List<Wallet> findByOwnerId(Long id) {
                return wallets.values().stream()
                        .filter(w -> w.getOwnerId().equals(id))
                        .collect(Collectors.toList());
            }

            @Override
            public Optional<Wallet> findById(Long id) {
                return Optional.ofNullable(wallets.get(id));
            }

            @Override
            public Wallet save(Wallet wallet) {
                return wallets.put(wallet.getId(), wallet);
            }

            @Override
            public void deleteById(Long id) {
                wallets.remove(id);
            }
        };
    }
}