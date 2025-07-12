package com.HabeshaTreasure.HabeshaTreasure.Service;

import com.HabeshaTreasure.HabeshaTreasure.DTO.TelebirrTransactionDetails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TelebirrVerificationService {

    public TelebirrTransactionDetails verify(String transactionId) throws IOException {
        String url = "https://transactioninfo.ethiotelecom.et/receipt/" + transactionId;

        Document doc = Jsoup.connect(url).get();

        if (doc.text().contains("This request is not correct")) {
            throw new IllegalArgumentException("Invalid or incorrect transaction ID.");
        }

        String payerName = safeSelectText(doc, "td:contains(Payer Name) + td");
        String creditedParty = safeSelectText(doc, "td:contains(Credited Party name) + td");
        String status = safeSelectText(doc, "td:contains(transaction status) + td");
        String bankReference = safeSelectText(doc, "#paid_reference_number");

        // ✅ Select the "Invoice details" table rows
        Element invoiceRow = doc.select("table").select("tr:has(td:contains(Invoice No.))").first();
        Element paymentRow = invoiceRow != null ? invoiceRow.nextElementSibling() : null;


        if (paymentRow == null || paymentRow.select("td").size() < 3) {
            throw new IllegalArgumentException("Could not extract payment date and amount.");
        }

        String date = paymentRow.select("td").get(1).text().trim();
        String amountText = paymentRow.select("td").get(2).text().replace("Birr", "").replace(",", "").trim();
        double amount = Double.parseDouble(amountText);

        System.out.println("date: " + date);
        System.out.println("amount: " + amount);
        System.out.println("payerName: " + payerName);
        System.out.println("creditedParty: " + creditedParty);

        return new TelebirrTransactionDetails(
                transactionId,
                payerName,
                creditedParty,
                status,
                bankReference,
                date,
                amount
        );
    }

    private String safeSelectText(Document doc, String cssQuery) {
        Element element = doc.selectFirst(cssQuery);
        return element != null ? element.text().trim() : "";
    }
}