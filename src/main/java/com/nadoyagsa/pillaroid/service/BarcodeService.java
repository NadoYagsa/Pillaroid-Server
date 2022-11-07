package com.nadoyagsa.pillaroid.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.math3.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BarcodeService {
	public String crawlSerialNumber(String barcode) throws IOException {
		if (!barcode.matches("^\\d{13,14}")) { // 의약품바코드 형식(GTIN-13,GTIN-14)
			throw BadRequestException.NOT_SUPPORTED_BARCODE_FORMAT;
		}

		// 구글에서 의약품안전나라 링크 크롤링
		String link = crawlProductLink(barcode);

		// 의약품안전나라에서 품목기준코드, 제품명 크롤링 (pair: 품목기준코드, 제품명)
		Pair<String, String> productInfo = crawlProductMeta(link, barcode);

		return productInfo.getFirst();
	}

	//품목기준코드와 품목명 반환
	private Pair<String, String> crawlProductMeta(String url, String barcode) throws IOException {
		String SerialNumber;
		String productName;

		Document nedrugDoc = Jsoup.connect(url).get();	// 의약품안전나라 정보 크롤링

		// 바코드가 정확히 일치하는 의약품인지 검사
		if (!hasBarcode(barcode, nedrugDoc)) {
			throw NotFoundException.BARCODE_NOT_FOUND;
		}

		// 품목기준코드 추출
		Optional<Element> SerialNumberTitle = nedrugDoc.select("th[scope=\"row\"]").stream()
				.filter(e -> e.text().equals("품목기준코드"))
				.findFirst();
		SerialNumber = SerialNumberTitle.map(element -> Objects.requireNonNull(element.nextElementSibling()).text()).orElse("");	//SerialNumberTitle 값이 없으면 빈 string 반환

		// 제품명 추출
		Element titleElement = nedrugDoc.selectFirst("h1 strong");
		productName = titleElement != null ? titleElement.text() : null;

		return new Pair<>(SerialNumber, productName);
	}

	private boolean hasBarcode(String barcode, Document nedrugDoc) {
		// 바코드 크롤링
		Optional<String> barcodeText = nedrugDoc.select("th[scope=\"row\"]").stream()
				.filter(e -> e.text().equals("표준코드"))
				.findFirst()
				.map(Element::parent)
				.map(e -> e.selectFirst("td"))
				.map(Element::text);

		// 크롤링 값 중 검색 바코드와 일치하는 것이 있는지 체크
		if (barcodeText.isPresent()) {
			String[] barcodes = barcodeText.get().split(", ");
			long resultCount = Arrays.stream(barcodes)
					.filter(b -> b.equals(barcode))
					.count();
			return resultCount == 1;	// 결과가 없으면 false 반환
		} else {
			throw NotFoundException.BARCODE_NOT_FOUND;
		}
	}

	private String crawlProductLink(String barcode) throws IOException {
		String barcodeForSearch = "\"" + barcode + "\"";
		String encodingBarcode = URLEncoder.encode(barcodeForSearch, StandardCharsets.UTF_8);
		String url = "https://www.google.com/search?q=nedrug " + encodingBarcode;	// 바코드 구글링(따옴표를 통해 무조건 내용에 있도록)
		Document googleDoc = Jsoup.connect(url).get();

		String nedrugUrl = "";
		Elements searchResults = googleDoc.select("div[class=\"yuRUbf\"]");
		for (Element e: searchResults) {
			String elementUrl = Objects.requireNonNull(e.selectFirst("a"))
					.attr("href");

			if (elementUrl.contains("https://nedrug.mfds.go.kr") && elementUrl.contains("getItemDetail?itemSeq=")) {	// 첫번째로 나오는 의약품안전나라 의약품 페이지 크롤링
				nedrugUrl = elementUrl;
				break;
			}
		}

		if (nedrugUrl.equals("")) throw NotFoundException.BARCODE_NOT_FOUND;	// 조회된 결과가 없으면 BARCODE_NOT_FOUND 익셉션

		return nedrugUrl;
	}
}
