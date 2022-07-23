package pl.sk.coinTracker.CategoryContent;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Data
@Table(name = "categories_content")
@Entity
@NoArgsConstructor
public class CategoryContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private Long categoryId;
    @NonNull
    private Long coinId;

    public  CategoryContent(Long categoryId, Long coinId){
        this.categoryId=categoryId;
        this.coinId=coinId;
    }
}
