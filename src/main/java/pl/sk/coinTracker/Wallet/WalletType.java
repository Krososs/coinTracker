package pl.sk.coinTracker.Wallet;

import java.util.ArrayList;
import java.util.List;

public enum WalletType {
    CUSTOM,
    ON_CHAIN,
    EXCHANGE;

    public static List<String> getAll() {
        List<String> walletTypes = new ArrayList<>();

        for (WalletType t : values()) {
            walletTypes.add(t.toString());
        }
        return walletTypes;
    }

    public static boolean exists(String t) {
        for (WalletType type : values()) {
            if (type.toString().equals(t))
                return true;
        }
        return false;
    }
}
