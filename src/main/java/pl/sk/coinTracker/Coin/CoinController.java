package pl.sk.coinTracker.Coin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sk.coinTracker.Support.Response;
import pl.sk.coinTracker.Support.Validation;

@RestController
public class CoinController {

    private final CoinService coinService;

    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    @GetMapping("coins/search")
    public ResponseEntity<?> search(@RequestParam String phrase) {
        return new ResponseEntity<>(coinService.getCoinsByPhrase(phrase), HttpStatus.OK);
    }

    @GetMapping("coins/info")
    public ResponseEntity<?> getCoinInfo(@RequestParam Long coinId) {
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(coinService.getCoinById(coinId), HttpStatus.OK);
    }
}
