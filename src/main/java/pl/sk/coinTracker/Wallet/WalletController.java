package pl.sk.coinTracker.Wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
import java.util.*;
import java.util.stream.Collectors;

import static pl.sk.coinTracker.Support.Validation.TRANSACTION_NOTE_MAX_LENGTH;
import static pl.sk.coinTracker.Support.Validation.WALLET_NAME_MAX_LENGTH;

@RestController
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;
    private final CoinService coinService;
    private final TransactionService transactionService;

    public WalletController(WalletService walletService, UserService userService, CoinService coinService, TransactionService transactionService) {
        this.walletService = walletService;
        this.userService = userService;
        this.coinService = coinService;
        this.transactionService = transactionService;
    }

    @PostMapping("wallets/create")
    public ResponseEntity<?> createWallet(@ModelAttribute Wallet wallet, @RequestHeader("authorization") String token) {

        String username = AuthUtil.getUsernameFromToken(token);
        User user = userService.getUserFromUsernamne(username);

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

        walletService.createNewWallet(wallet, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("wallets/edit")
    public ResponseEntity<?> editWallet(@ModelAttribute Wallet wallet, @RequestHeader("authorization") String token) {

        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        if (!walletService.walletExists(wallet.getId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, wallet.getId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (wallet.getName().length() == 0)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_WALLET_NAME.ToString()), HttpStatus.CONFLICT);
        if (wallet.getName().length() > WALLET_NAME_MAX_LENGTH)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_NAME_TOO_LONG.ToString()), HttpStatus.CONFLICT);

        walletService.editWallet(wallet, wallet.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("wallets/delete")
    public ResponseEntity<?> deleteWallet(@RequestParam Long walletId, @RequestHeader("authorization") String token) {

        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        transactionService.deleteTransactionsByWalletId(walletId);
        walletService.deleteWallet(walletId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("wallets/coin/add")
    public ResponseEntity<?> addNewCoin(@RequestParam Long walletId, @RequestParam Long coinId, @RequestHeader("authorization") String token) {

        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));

        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (transactionService.walletContainsCoin(walletId, coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_ALREADY_CONTAINS_COIN.ToString()), HttpStatus.CONFLICT);

        transactionService.addNewTransaction(new Transaction(coinId, walletId, "BUY", 0.0, 0.0, new Date(), null));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("wallets/coin/delete")
    public ResponseEntity<?> deleteCoin(@RequestParam Long walletId, @RequestParam Long coinId, @RequestHeader("authorization") String token) {

        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));

        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        transactionService.deleteTransactionsByCoinId(walletId, coinId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("wallets/transactions/add")
    public ResponseEntity<?> addTransaction(@ModelAttribute Transaction transaction, @RequestHeader("authorization") String token) {

        if (transaction.getAmount() <= 0.0)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WRONG_TRANSACTION_AMOUNT.ToString()), HttpStatus.CONFLICT);
        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        if (!walletService.userIsOwner(userId, transaction.getWalletId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (!transactionService.walletContainsCoin(transaction.getWalletId(), transaction.getCoinId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_CONTAIN_COIN.ToString()), HttpStatus.NOT_FOUND);
        if (transaction.getNote().length() > TRANSACTION_NOTE_MAX_LENGTH)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TRANSACTION_NOTE_TOO_LONG.ToString()), HttpStatus.CONFLICT);

        transactionService.addNewTransaction(transaction);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("wallets/transactions/delete")
    public ResponseEntity<?> deleteTransaction(@RequestParam Long transactionId, @RequestHeader("authorization") String token) {

        if (!transactionService.transactionExists(transactionId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TRANSACTION_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);

        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        Long walletId = transactionService.getTransactionById(transactionId).getWalletId();

        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        transactionService.deleteTransactionById(transactionId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("wallets/transactions/edit")
    public ResponseEntity<?> editTransaction(@ModelAttribute Transaction transaction, @RequestHeader("authorization") String token) {

        if (transaction.getId() == null || !transactionService.transactionExists(transaction.getId()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TRANSACTION_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);

        Transaction t = transactionService.getTransactionById(transaction.getId());
        transaction.setWalletId(t.getWalletId());
        transaction.setCoinId(t.getCoinId());

        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        Long walletId = transaction.getWalletId();

        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (transaction.getNote().length() > TRANSACTION_NOTE_MAX_LENGTH)
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TRANSACTION_NOTE_TOO_LONG.ToString()), HttpStatus.CONFLICT);

        transactionService.editTransaction(transaction);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("wallets/info")
    public ResponseEntity<?> getWalletInfo(@RequestParam Long walletId, @RequestHeader("authorization") String token) {

        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        List<Transaction> walletTransactions = transactionService.getTransactionsByWalletId(walletId);
        Map<Long, Double> coins = new HashMap<>(); // id/amount
        double totalSpend = 0.0;

        for (Transaction t : walletTransactions) {

            if (t.getType().equals("BUY")) {
                if (coins.get(t.getCoinId()) == null)
                    coins.put(t.getCoinId(), t.getAmount());
                else
                    coins.put(t.getCoinId(), coins.get(t.getCoinId()) + t.getAmount());
                totalSpend += (t.getAmount() * t.getPrice());
            } else {
                if (coins.get(t.getCoinId()) == null)
                    coins.put(t.getCoinId(), -t.getAmount());
                else
                    coins.put(t.getCoinId(), coins.get(t.getCoinId()) - t.getAmount());
                totalSpend -= (t.getAmount() * t.getPrice());
            }
        }

        ObjectNode walletInfo = new ObjectMapper().createObjectNode();
        ArrayNode coinsInfo = walletInfo.putArray("coins");
        double totalValue = 0.0;
        Double price;
        Double value;

        for (Map.Entry<Long, Double> coin : coins.entrySet()) {

            price = coinService.getPrice(coin.getKey());
            value = price * coin.getValue();
            ObjectNode coinInfo = coinService.getCoinInfo(coin.getKey(), coin.getValue(), price, value, transactionService.getInitialTransaction(walletId, coin.getKey()).getDate().toString());
            coinsInfo.add(coinInfo);
            totalValue += value;
        }

        Wallet wallet = walletService.getWalletById(walletId);

        double pnl = walletService.getPercentageChange(totalSpend, totalValue);
        if(totalValue> wallet.getAth()) {
            wallet.setAth(totalValue);
            walletService.editWallet(wallet,walletId);
        }

        walletInfo.put("name", wallet.getName());
        walletInfo.put("id", walletId);
        walletInfo.put("type", wallet.getType().toString());
        walletInfo.put("ath",Math.round(wallet.getAth() * 100.0) / 100.0);
        walletInfo.put("totalValue", Math.round(totalValue * 100.0) / 100.0);
        walletInfo.put("totalSpend", Math.round(totalSpend * 100.0) / 100.0);
        walletInfo.put("pnl", Math.round(pnl * 100.0) / 100.0);
        return new ResponseEntity<>(walletInfo, HttpStatus.OK);
    }

    @GetMapping("wallets/chain/info")
    public ResponseEntity<?> getOnChainWalletInfo(@RequestParam String chain, @RequestParam Long walletId, @RequestHeader("authorization") String token) throws IOException, InterruptedException {

        if (!Chain.exists(chain))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.CHAIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);

        Scrapper s = Scrapper.get(chain);

        String address = walletService.getWalletById(walletId).getAddress();
        List<Map<String, String>> tokens = s.getAccountTokens(address);

        ObjectNode walletInfo = new ObjectMapper().createObjectNode();
        ArrayNode coinsInfo = walletInfo.putArray("coins");

        Long id = coinService.getCoinByTicker(s.getNativeCurrencyTicker()).getId();
        Double price = coinService.getPrice(id);
        Double value = price * s.getNativeCurrencyBalance(address).get("balance");
        Double totalValue = 0.0;

        ObjectNode coinInfo = coinService.getCoinInfo(id, s.getNativeCurrencyBalance(address).get("balance"), price, value, "none");
        coinsInfo.add(coinInfo);
        totalValue += value;

        for (Map<String, String> t : tokens) {

            if (coinService.coinExistsByTicker(t.get("ticker"))) {
                id = coinService.getCoinByTicker(t.get("ticker")).getId();
                price = coinService.getPrice(id);
                value = price * Double.valueOf(t.get("balance"));
                coinInfo = coinService.getCoinInfo(id, Double.valueOf(t.get("balance")), price, value, "none");
                coinsInfo.add(coinInfo);
                totalValue += value;
            }
        }
        Wallet wallet = walletService.getWalletById(walletId);

        if(totalValue> wallet.getAth()) {
            wallet.setAth(totalValue);
            walletService.editWallet(wallet,walletId);
        }

        walletInfo.put("name", walletService.getWalletById(walletId).getName());
        walletInfo.put("type", walletService.getWalletById(walletId).getType().toString());
        walletInfo.put("id", walletId);
        walletInfo.put("ath",Math.round(wallet.getAth() * 100.0) / 100.0);
        walletInfo.put("totalValue", Math.round(totalValue * 100.0) / 100.0);

        return new ResponseEntity<>(walletInfo, HttpStatus.OK);
    }

    @GetMapping("wallets/types")
    public ResponseEntity<?> getWalletTypes() {
        return new ResponseEntity<>(WalletType.getAll(), HttpStatus.OK);
    }

    @GetMapping("wallets/all")
    public ResponseEntity<?> getUserWallets(@RequestHeader("authorization") String token) {
        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        List<Wallet> wallets = walletService.getUserWallets(userId);
        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }

    @GetMapping("wallets/transactions/get")
    public ResponseEntity<?> getTransactions(@RequestParam Long walletId, @RequestParam Long coinId, @RequestHeader("authorization") String token) {

        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);

        List<Transaction> transactions = transactionService.getTransactions(walletId, coinId)
                .stream().filter(t -> t.getAmount() > 0.0).collect(Collectors.toList());

        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("wallets/transactions/get/all")
    public ResponseEntity<?> getAllTransactions(@RequestParam Long walletId, @RequestHeader("authorization") String token) {
        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        if (!walletService.walletExists(walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.WALLET_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!walletService.userIsOwner(userId, walletId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_WALLET.ToString()), HttpStatus.CONFLICT);

        return new ResponseEntity<>(transactionService.getTransactionsByWalletId(walletId), HttpStatus.OK);
    }
}
