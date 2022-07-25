package pl.sk.coinTracker.Transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NonNull;
import pl.sk.coinTracker.Wallet.Wallet;

import javax.persistence.*;
import java.util.Date;

@Table(name = "transactions")
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private Date date;
    @NonNull
    private Long coinId;
    @NonNull
    private Double price;
    @NonNull
    private Double amount;
    @NonNull
    private String type;
    private String note;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties(value = {"transactions", "handler", "hibernateLazyInitializer"}, allowSetters = true)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    public Transaction(Long coinId, String type,Wallet wallet, double amount, double price, Date date, String note) {
        this.coinId = coinId;
        this.type = type;
        this.wallet=wallet;
        this.amount = amount;
        this.price = price;
        this.date = date;
        this.note = note;
    }

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Long getCoinId() {
        return coinId;
    }

    public Double getPrice() {
        return price;
    }

    public Double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getNote() {
        return note;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCoinId(Long coinId) {
        this.coinId = coinId;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
