package pl.sk.coinTracker.Transaction;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Date;

@Data
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
    private Long walletId;
    @NonNull
    private Double price;
    @NonNull
    private Double amount;
    @NonNull
    private String type;
    private String note;

    public Transaction(Long coinId, Long walletId, String type, double amount, double price, Date date, String note) {
        this.coinId = coinId;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.price = price;
        this.date = date;
        this.note = note;
    }

    public Transaction() {
    }
}
