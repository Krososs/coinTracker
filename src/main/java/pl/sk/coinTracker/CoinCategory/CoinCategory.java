package pl.sk.coinTracker.CoinCategory;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Table(name = "categories")
@Entity
@NoArgsConstructor
public class CoinCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private Long userId;
    @NonNull
    @Size(min = 1, max = 25,message= "Category name should contain from 1 to 25 characters")
    private String name;
}
