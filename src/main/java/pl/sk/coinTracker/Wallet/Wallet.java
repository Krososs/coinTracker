package pl.sk.coinTracker.Wallet;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;

@Data
@Table(name="wallets")
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

    public Wallet() {
    }
}
