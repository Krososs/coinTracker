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
import pl.sk.coinTracker.Support.Chain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scrapper {

    private final String accountAddress;
    private final Chain chain;

    public Scrapper(String accountAddress, Chain chain){
        this.accountAddress=accountAddress;
        this.chain=chain;
    }

    public static Scrapper get(String accountAddress, String chain){
        return switch (chain) {
            case "ETHEREUM" -> new Scrapper(accountAddress, Chain.ETHEREUM);
            case "BINANCE_SMART_CHAIN" -> new Scrapper(accountAddress, Chain.BINANCE_SMART_CHAIN);
            default -> new Scrapper(accountAddress, Chain.ETHEREUM);
        };
    }

    public  List<Map<String, String>> getAccountTokens(String accountAddress) throws IOException{
        WebClient webClient = setUpWebClient();
        HtmlPage htmlPage = webClient.getPage(getUrl());
        Document parsedDocument = Jsoup.parse(htmlPage.asXml());
        Elements tokens = parsedDocument.select("div.card.h-100")
                .select("div[id=ContentPlaceholder1_tokenbalance]")
                .select("ul.list.list-unstyled.mb-0");

        List<Map<String, String>> account = new ArrayList<>();
        for (Element e : tokens.select(getCSSTokenListClass())) {

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

    public  Map<String, Double> getNativeCurrencyBalance() throws IOException{
        WebClient webClient = setUpWebClient();
        HtmlPage htmlPage = webClient.getPage(getUrl());
        Document parsedDocument = Jsoup.parse(htmlPage.asXml());

        Element nativeBalance = parsedDocument.select("div.card.h-100")
                .select("div.card-body")
                .select("div.col-md-8")
                .first();

        String text = nativeBalance.getElementsByTag("div").text();
        text = StringUtils.deleteWhitespace(removeLetters(text));
        Map<String, Double> balance = new HashMap<>();
        balance.put("balance", Double.valueOf(text));
        return balance;
    }

    public  String getNativeCurrencyTicker(){
        return  switch (chain){
            case BINANCE_SMART_CHAIN -> "bnb";
            case ETHEREUM -> "eth";
        };
    }

    private String getUrl(){
        return  switch (chain){
            case BINANCE_SMART_CHAIN -> "https://bscscan.com//address/"+accountAddress;
            case ETHEREUM -> "https://etherscan.io//address/"+accountAddress;
        };
    }

    private String getCSSTokenListClass(){
        return switch (chain){
            case BINANCE_SMART_CHAIN -> "li.list-custom.list-custom-BEP-20";
            case ETHEREUM -> "li.list-custom.list-custom-ERC20";
        };
    }

    public String removeLetters(String expression) {
        StringBuffer buffer = new StringBuffer(expression);

        for (int i = 0; i < expression.length(); i++) {
            if (Character.isLetter(expression.charAt(i)))
                buffer.replace(i, i + 1, " ");
        }
        return buffer.toString();
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
