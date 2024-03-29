package pl.sk.coinTracker.Wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sk.coinTracker.BlacklistedCoin.BlacklistedCoin;
import pl.sk.coinTracker.BlacklistedCoin.BlacklistedCoinService;
import pl.sk.coinTracker.Coin.CoinService;
import pl.sk.coinTracker.Scrapers.Scrapper;
import pl.sk.coinTracker.Support.Chain;
import pl.sk.coinTracker.Support.Response;
import pl.sk.coinTracker.Support.Validation;
import pl.sk.coinTracker.Transaction.Transaction;
import pl.sk.coinTracker.Transaction.TransactionService;
import pl.sk.coinTracker.User.User;
import pl.sk.coinTracker.User.UserService;
import pl.sk.coinTracker.Security.AuthUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static pl.sk.coinTracker.Support.Validation.TRANSACTION_NOTE_MAX_LENGTH;
import static pl.sk.coinTracker.Support.Validation.WALLET_NAME_MAX_LENGTH;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;
    private final CoinService coinService;
    private final BlacklistedCoinService blacklistedCoinService;
    private final TransactionService transactionService;

    public WalletController(WalletService walletService, UserService userService, CoinService coinService, BlacklistedCoinService blacklistedCoinService, TransactionService transactionService) {
        this.walletService = walletService;
        this.userService = userService;
        this.coinService = coinService;
        this.blacklistedCoinService = blacklistedCoinService;
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createWallet(@ModelAttribute Wallet wallet, Principal p) {

        User user = userService.getUserFromUsernamne(p.getName());

        if (wallet.getName().length() == 0)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_WALLET_NAME.ToString()), HttpStatus.CONFLICT);
        if (wallet.getName().length() > WALLET_NAME_MAX_LENGTH)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_NAME_TOO_LONG.ToString()), HttpStatus.CONFLICT);
        if (walletService.walletNameAlreadyExists(wallet.getName(), user.getId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_ALREADY_EXIST.ToString()), HttpStatus.CONFLICT);
        if (!WalletType.exists(wallet.getType().toString()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_WALLET_TYPE.ToString()), HttpStatus.NOT_FOUND);
        if (wallet.getType().equals(WalletType.ON_CHAIN) && (wallet.getAddress() == null || wallet.getAddress().length() == 0))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_ADDRESS.ToString()), HttpStatus.CONFLICT);

        walletService.createNewWallet(wallet, user.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/edit")
    public ResponseEntity<?> editWallet(@ModelAttribute Wallet wallet, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());
        if (!walletService.walletExists(wallet.getId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, wallet.getId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (wallet.getName().length() == 0)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_WALLET_NAME.ToString()), HttpStatus.CONFLICT);
        if (wallet.getName().length() > WALLET_NAME_MAX_LENGTH)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_NAME_TOO_LONG.ToString()), HttpStatus.CONFLICT);

        walletService.editWallet(wallet.getName(), wallet.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteWallet(@RequestParam Long walletId, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        walletService.deleteWallet(walletId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/coin/add")
    public ResponseEntity<?> addNewCoin(@RequestParam Long walletId, @RequestParam Long coinId, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());

        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (transactionService.walletContainsCoin(walletId, coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_ALREADY_CONTAINS_COIN.ToString()), HttpStatus.CONFLICT);

        transactionService.addNewTransaction(new Transaction(coinId, "BUY", walletService.getWalletById(walletId), BigDecimal.ZERO, BigDecimal.ZERO, new Date(), null));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/coin/delete")
    public ResponseEntity<?> deleteCoin(@RequestParam Long walletId, @RequestParam Long coinId, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());

        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        transactionService.deleteTransactionsByCoinId(walletId, coinId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/blacklist/get")
    public ResponseEntity<?> getBlacklistedCoins(@RequestParam Long walletId, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.getWalletById(walletId).getType().equals(WalletType.ON_CHAIN))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_WALLET_TYPE.ToString()), HttpStatus.CONFLICT);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        return new ResponseEntity<>(blacklistedCoinService.getBlacklistedCoins(walletId), HttpStatus.OK);
    }


    @PostMapping("/blacklist/add")
    public ResponseEntity<?> addCoinToTheBlacklist(@RequestParam Long walletId, @RequestParam Long coinId, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.getWalletById(walletId).getType().equals(WalletType.ON_CHAIN))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_WALLET_TYPE.ToString()), HttpStatus.CONFLICT);
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (blacklistedCoinService.coinIsBlacklisted(walletId, coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_ALREADY_BLACKLISTED.ToString()), HttpStatus.CONFLICT);

        blacklistedCoinService.addCoinToBlackList(new BlacklistedCoin(walletId, coinId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/blacklist/remove")
    public ResponseEntity<?> removeCoinFromBlackList(@RequestParam Long walletId, @RequestParam Long coinId, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.getWalletById(walletId).getType().equals(WalletType.ON_CHAIN))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_WALLET_TYPE.ToString()), HttpStatus.CONFLICT);
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (!blacklistedCoinService.coinIsBlacklisted(walletId, coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_NOT_BLACKLISTED.ToString()), HttpStatus.CONFLICT);

        blacklistedCoinService.removeCoinFromBlackList(walletId, coinId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/transactions/add")
    public ResponseEntity<?> addTransaction(@RequestParam Long walletId, @ModelAttribute Transaction transaction, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_TRANSACTION_AMOUNT.ToString()), HttpStatus.CONFLICT);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (!transactionService.walletContainsCoin(walletId, transaction.getCoinId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_CONTAIN_COIN.ToString()), HttpStatus.NOT_FOUND);
        if (transaction.getNote().length() > TRANSACTION_NOTE_MAX_LENGTH)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TRANSACTION_NOTE_TOO_LONG.ToString()), HttpStatus.CONFLICT);
        transaction.setWallet(walletService.getWalletById(walletId));
        transactionService.addNewTransaction(transaction);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/transactions/delete")
    public ResponseEntity<?> deleteTransaction(@RequestParam Long transactionId, Principal p) {

        if (!transactionService.transactionExists(transactionId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TRANSACTION_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);

        Long userId = userService.getUserIdFromUsername(p.getName());
        Long walletId = transactionService.getTransactionById(transactionId).getWallet().getId();

        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        transactionService.deleteTransactionById(transactionId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/transactions/edit")
    public ResponseEntity<?> editTransaction(@ModelAttribute Transaction transaction, Principal p) {

        if (transaction.getId() == null || !transactionService.transactionExists(transaction.getId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TRANSACTION_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);

        Transaction t = transactionService.getTransactionById(transaction.getId());
        transaction.setWallet(t.getWallet());
        transaction.setCoinId(t.getCoinId());

        Long userId = userService.getUserIdFromUsername(p.getName());
        Long walletId = transaction.getWallet().getId();

        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (transaction.getNote().length() > TRANSACTION_NOTE_MAX_LENGTH)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TRANSACTION_NOTE_TOO_LONG.ToString()), HttpStatus.CONFLICT);

        transactionService.editTransaction(transaction);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getWalletInfo(@RequestParam Long walletId, Principal p) {

        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        Long userId = userService.getUserIdFromUsername(p.getName());
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        Wallet wallet = walletService.getWalletById(walletId);
        List<Transaction> walletTransactions = wallet.getTransactions();
        Map<Long, BigDecimal> coinsAmount = walletService.countCoinsAmount(walletTransactions); // id/amount

        BigDecimal totalSpend = walletService.getTotalSpend(walletTransactions);
        BigDecimal totalValue = BigDecimal.ZERO;

        ObjectNode walletInfo = new ObjectMapper().createObjectNode();
        ArrayNode coinsInfo = walletInfo.putArray("coinsAmount");

        BigDecimal price;
        BigDecimal value;

        for (Map.Entry<Long, BigDecimal> coin : coinsAmount.entrySet()) {

            price = coinService.getPrice(coin.getKey());
            value = price.multiply(coin.getValue());
            ObjectNode coinInfo = coinService.getCoinInfo(coin.getKey(), coin.getValue(), price, value, transactionService.getInitialTransaction(walletId, coin.getKey()).getDate().toString());
            coinsInfo.add(coinInfo);
            totalValue = totalValue.add(value);
        }

        BigDecimal pnl = walletService.getPercentageChange(totalSpend, totalValue);

        if (totalValue.compareTo(wallet.getAth()) > 0) {
            wallet.setAth(totalValue);
            walletService.editWallet(wallet.getName(), walletId);
        }

        walletInfo.put("name", wallet.getName());
        walletInfo.put("id", walletId);
        walletInfo.put("type", wallet.getType().toString());
        walletInfo.put("ath", wallet.getAth().round(new MathContext(4)));
        walletInfo.put("totalValue", totalValue.round(new MathContext(4)));
        walletInfo.put("totalSpend", totalSpend.round(new MathContext(4)));
        walletInfo.put("pnl", pnl.round(new MathContext(4)));
        return new ResponseEntity<>(walletInfo, HttpStatus.OK);
    }

    @GetMapping("/chain/info")
    public ResponseEntity<?> getOnChainWalletInfo(@RequestParam String chain, @RequestParam Long walletId, Principal p) throws IOException {

        if (!Chain.exists(chain))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.CHAIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        Long userId = userService.getUserIdFromUsername(p.getName());
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);


        String address = walletService.getWalletById(walletId).getAddress();
        Scrapper s = Scrapper.get(address, chain);

        List<Map<String, String>> tokens = s.getAccountTokens(address);

        ObjectNode walletInfo = new ObjectMapper().createObjectNode();
        ArrayNode coinsInfo = walletInfo.putArray("coinsAmount");

        Long id = coinService.getCoinByTicker(s.getNativeCurrencyTicker()).getId();
        BigDecimal price = coinService.getPrice(id);
        BigDecimal value = price.multiply(BigDecimal.valueOf(s.getNativeCurrencyBalance().get("balance")));
        BigDecimal totalValue = BigDecimal.ZERO;

        ObjectNode coinInfo = coinService.getCoinInfo(id, BigDecimal.valueOf(s.getNativeCurrencyBalance().get("balance")), price, value, "none");
        coinsInfo.add(coinInfo);
        totalValue = totalValue.add(value);

        for (Map<String, String> t : tokens) {

            if (coinService.coinExistsByTicker(t.get("ticker"))) {
                id = coinService.getCoinByTicker(t.get("ticker")).getId();
                price = coinService.getPrice(id);
                value = price.multiply(new BigDecimal(t.get("balance").replaceAll(",", "")));
                coinInfo = coinService.getCoinInfo(id, new BigDecimal(t.get("balance").replaceAll(",", "")), price, value, "none");
                coinsInfo.add(coinInfo);
                totalValue = totalValue.add(value);
            }
        }

        Wallet wallet = walletService.getWalletById(walletId);

        if (totalValue.compareTo(wallet.getAth()) > 0) {
            wallet.setAth(totalValue);
            walletService.editWallet(wallet.getName(), walletId);
        }

        walletInfo.put("name", wallet.getName());
        walletInfo.put("type", wallet.getType().toString());
        walletInfo.put("id", walletId);
        walletInfo.put("ath", wallet.getAth().round(new MathContext(3)));
        walletInfo.put("totalValue", totalValue.round(new MathContext(3)));

        return new ResponseEntity<>(walletInfo, HttpStatus.OK);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getWalletTypes() {
        return new ResponseEntity<>(WalletType.getAll(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getUserWallets(@RequestHeader("authorization") String token) {
        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        List<Wallet> wallets = walletService.getUserWallets(userId);
        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }

    @GetMapping("/transactions/get")
    public ResponseEntity<?> getTransactions(@RequestParam Long walletId, @RequestParam Long coinId, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);

        List<Transaction> transactions = transactionService.getTransactions(walletId, coinId)
                .stream().filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/transactions/get/all")
    public ResponseEntity<?> getAllTransactions(@RequestParam Long walletId, Principal p) {
        Long userId = userService.getUserIdFromUsername(p.getName());
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        return new ResponseEntity<>(transactionService.getTransactionsByWallet(walletService.getWalletById(walletId)), HttpStatus.OK);
    }
}
