package com.nadoyagsa.pillaroid.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.math3.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.nadoyagsa.pillaroid.common.exception.ProjectException;
import com.nadoyagsa.pillaroid.dto.BarcodeCrawlingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BarcodeService {
	public BarcodeCrawlingResponse getProductCode(String barcode) throws IOException {
		if (!barcode.matches("^\\d{13,14}")) { //의약품바코드 형식(GTIN-13,GTIN-14)
			throw ProjectException.NOT_SUPPORTED_BARCODE_FORMAT;
		}

		// 구글에서 의약품안전나라 링크 크롤링
		String link = crawlProductLink(barcode);

		//의약품안전나라에서 품목기준코드, 제품명 크롤링 (pair: 품목기준코드, 제품명)
		Pair<String, String> productInfo = crawlProductMeta(link);

		return BarcodeCrawlingResponse
				.builder()
				.productCode(productInfo.getFirst())
				.productName(productInfo.getSecond())
				.build();
	}

	private Pair<String, String> crawlProductMeta(String url) throws IOException {
		String productCode;
		String productName;

		Document nedrugDoc = Jsoup.connect(url).get();	//의약품안전나라 정보 크롤링

		Optional<Element> productCodeTitle = nedrugDoc.select("th[scope=\"row\"]").stream()
				.filter(e -> e.text().equals("품목기준코드"))
				.findFirst();

		productCode = productCodeTitle.map(element -> Objects.requireNonNull(element.nextElementSibling()).text()).orElse("");

		Element titleElement = nedrugDoc.selectFirst("h1 strong");
		productName = titleElement != null ? titleElement.text() : null;

		return new Pair<>(productCode, productName);
	}

	private String crawlProductLink(String barcode) throws IOException {
		String barcodeForSearch = "\"" + barcode + "\"";
		String encodingBarcode = URLEncoder.encode(barcodeForSearch, StandardCharsets.UTF_8);
		String url = "https://www.google.com/search?q=nedrug " + encodingBarcode;	//바코드 구글링(따옴표를 통해 무조건 내용에 있도록)
		Document googleDoc = Jsoup.connect(url).get();

		String nedrugUrl = "";
		Elements searchResults = googleDoc.select("div[class=\"yuRUbf\"]");
		for (Element e: searchResults) {
			String elementUrl = Objects.requireNonNull(e.selectFirst("a"))
					.attr("href");

			if (elementUrl.contains("https://nedrug.mfds.go.kr") && elementUrl.contains("getItemDetail?itemSeq=")) {	//첫번째로 나오는 의약품안전나라 의약품 페이지 크롤링
				nedrugUrl = elementUrl;
				break;
			}
		}

		if (nedrugUrl.equals("")) throw ProjectException.BARCODE_NOT_FOUND;	//조회된 결과가 없으면 BARCODE_NOT_FOUND 익셉션

		return nedrugUrl;
	}
}
