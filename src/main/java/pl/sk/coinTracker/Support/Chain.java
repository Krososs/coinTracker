package pl.sk.coinTracker.Support;

import java.util.ArrayList;
import java.util.List;

public enum Chain {
    ETHEREUM,
    BINANCE_SMART_CHAIN,
    POLYGON;

    public static List<String> getAll() {
        List<String> chains = new ArrayList<>();

        for (Chain c : values()) {
            chains.add(c.toString());
        }
        return chains;
    }

    public static boolean exists(String c) {
        for (Chain chain : values()) {
            if (chain.toString().equals(c))
                return true;
        }
        return false;
    }
}
