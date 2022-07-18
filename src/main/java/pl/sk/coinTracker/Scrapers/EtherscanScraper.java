package pl.sk.coinTracker.Scrapers;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtherscanScraper extends Scrapper {

    private static final String ROOT_URL = "https://etherscan.io/";
    private static final String NATIVE_CURRENCY_TICKER = "eth";

    @Override
    public String getNativeCurrencyTicker() {
        return NATIVE_CURRENCY_TICKER;
    }

    @Override
    public Map<String, Double> getNativeCurrencyBalance(String accountAddress) throws IOException {

        WebClient webClient = setUpWebClient();
        HtmlPage htmlPage = webClient.getPage(ROOT_URL + "/address/" + accountAddress);
        Document parsedDocument = Jsoup.parse(htmlPage.asXml());

        Element ethBalance = parsedDocument.select("div.card.h-100")
                .select("div.card-body")
                .select("div.col-md-8")
                .first();

        String text = ethBalance.getElementsByTag("div").text();
        text = StringUtils.deleteWhitespace(removeLetters(text));

        Map<String, Double> balance = new HashMap<>();
        balance.put("balance", Double.valueOf(text));
        return balance;
    }

    @Override
    public List<Map<String, String>> getAccountTokens(String accountAddress) throws IOException {

        WebClient webClient = setUpWebClient();
        HtmlPage htmlPage = webClient.getPage(ROOT_URL + "/address/" + accountAddress);
        Document parsedDocument = Jsoup.parse(htmlPage.asXml());
        Elements tokens = parsedDocument.select("div.card.h-100")
                .select("div[id=ContentPlaceholder1_tokenbalance]")
                .select("ul.list.list-unstyled.mb-0");

        List<Map<String, String>> account = new ArrayList<>();
        for (Element e : tokens.select("li.list-custom.list-custom-ERC20")) {

            String name = e.select("span.list-name.hash-tag.text-truncate").text();
            String balance = e.select("span.list-amount.link-hover__item.hash-tag.hash-tag--md.text-truncate").text();
            balance = StringUtils.deleteWhitespace(removeLetters(balance));

            Map<String, String> coin = new HashMap<>();
            coin.put("ticker", name.substring(name.indexOf("(") + 1, name.indexOf(")")).toLowerCase());
            coin.put("balance", balance);
            account.add(coin);
        }
        return account;
    }

    private static WebClient setUpWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
        return webClient;
    }
}
