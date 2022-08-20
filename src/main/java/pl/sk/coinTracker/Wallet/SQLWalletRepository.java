package pl.sk.coinTracker.Wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SQLWalletRepository extends WalletRepository, JpaRepository<Wallet, Long> {
}
