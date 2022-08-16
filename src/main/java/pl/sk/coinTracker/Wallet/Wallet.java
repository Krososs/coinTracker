package pl.sk.coinTracker.Wallet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.sk.coinTracker.Transaction.Transaction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Table(name = "wallets")
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private Long ownerId;
    @NonNull
    private String name;
    @Enumerated(EnumType.ORDINAL)
    private WalletType type;
    private String address; // onChain address
    @NonNull
    private BigDecimal ath;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"wallet", "handler", "hibernateLazyInitializer"}, allowSetters = true)
    private List<Transaction> transactions;

    public Wallet() {
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public WalletType getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getAth() {
        return ath;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(WalletType type) {
        this.type = type;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAth(BigDecimal ath) {
        this.ath = ath;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
