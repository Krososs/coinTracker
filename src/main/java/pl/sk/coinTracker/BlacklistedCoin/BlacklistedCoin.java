package pl.sk.coinTracker.BlacklistedCoin;

import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name ="blacklisted_coins")
@Entity
public class BlacklistedCoin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long walletId;
    @NotNull
    private Long coinId;

    public BlacklistedCoin(){

    }

    public BlacklistedCoin(Long walletId, Long coinId){
        this.walletId=walletId;
        this.coinId=coinId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Long getCoinId() {
        return coinId;
    }

    public void setCoinId(Long coinId) {
        this.coinId = coinId;
    }
}
