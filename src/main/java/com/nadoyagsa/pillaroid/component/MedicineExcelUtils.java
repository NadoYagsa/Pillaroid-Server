package com.nadoyagsa.pillaroid.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.nadoyagsa.pillaroid.dto.Medicine;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MedicineExcelUtils {
	private final JsoupComponent jsoupComponent;

	public void updateMedicineExcel() throws IOException {

		ClassPathResource inputResource = new ClassPathResource("data/medicine.xlsx");

		FileInputStream file = new FileInputStream(new File(inputResource.getURI()));
		XSSFWorkbook workbook = new XSSFWorkbook(file);

		XSSFSheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		int titleColIdx = 0;	//의약품명이 적힌 열의 Idx

		for (int rowIdx = 1; rowIdx<rows; rowIdx++) {
			XSSFRow row = sheet.getRow(rowIdx);
			if (row != null) {
				//엑셀 파일에서 제품명 읽기
				XSSFCell cell = row.getCell(titleColIdx);
				String itemName = cell.getStringCellValue();

				// NAVER 지식백과에서 제품명에 대한 최상위 결과 조회
				String productLink = crawlProductLink(itemName);

				if (productLink != null) {
					// 세부정보에서 원하는 항목 추출
					HashMap<Integer, String> result = crawlMedicineInfo(productLink);

					//크롤링한 값을 셀에 저장
					result.forEach((colIdx, content) -> {
						XSSFCell editCell = row.getCell(colIdx);
						editCell.setCellValue(content);
					});
				} else {
					//값이 없으면 공백 저장
					for (int colIdx = 4; colIdx <= 6; colIdx++) {	//4: 효능효과, 5: 용법용량, 6: 주의사항
						XSSFCell editCell = row.getCell(colIdx);
						editCell.setCellValue("");
					}
				}
			}
		}

		//파일에 저장
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "/src/main/resources/data/medicine_result.xlsx";
		FileOutputStream outStream = new FileOutputStream(fileLocation);
		workbook.write(outStream);
		workbook.close();
	}

	private String crawlProductLink(String itemName) throws IOException {
		String encodingItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8);
		String wikipediaUrl = "https://terms.naver.com/medicineSearch.naver?mode=nameSearch&query="+ encodingItemName;
		Document wikipediaDoc = Jsoup.connect(wikipediaUrl).get();

		Element firstElement = wikipediaDoc.selectFirst("ul[class=\"content_list\"]");
		if (firstElement == null) return null;	//검색결과가 없으면 null 반환

		Element firstTitleElement = firstElement
				.selectFirst("li")
				.selectFirst("div[class=\"info_area\"]")
				.getElementsByClass("title").first()
				.selectFirst("a");
		return firstTitleElement.attr("href");
	}

	private HashMap<Integer, String> crawlMedicineInfo(String productLink) {
		String detailBaseUrl = "https://terms.naver.com";
		String detailUrl = detailBaseUrl + productLink;

		Medicine medicineInfo = jsoupComponent.getMedicineInfo(detailUrl);

		//(수정할 colIdx, content)로 된 hashMap
		HashMap<Integer, String> result = new HashMap<>();
		result.put(4, medicineInfo.getEfficacy());
		result.put(5, medicineInfo.getUsage());
		result.put(6, medicineInfo.getPrecautions());
		return result;
	}
}
