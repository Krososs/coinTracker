package pl.sk.coinTracker.Coin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import javax.persistence.*;

@Data
@Table(name = "coins")
@Entity
@NoArgsConstructor
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String ticker; //symbol
    @NonNull
    private String identifier; //coingecko api id
    @NonNull
    private Long coinrank;
    private String image;

    public Coin(String name, String ticker, String identifier, Long coinrank) {
        this.name = name;
        this.ticker = ticker;
        this.identifier = identifier;
        this.coinrank = coinrank;
    }

    public ObjectNode toJson() {
        return new ObjectMapper().createObjectNode()
                .put("id", this.id)
                .put("name", this.name)
                .put("ticker", this.ticker)
                .put("identifier", this.identifier)
                .put("rank", this.coinrank)
                .put("image", this.image);
    }
}
