package pl.sk.coinTracker.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SQLTransactionRepository extends TransactionRepository, JpaRepository<Transaction, Long> {
}
