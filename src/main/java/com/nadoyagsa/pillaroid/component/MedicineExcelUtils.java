package com.nadoyagsa.pillaroid.component;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.MedicineCrawl;
import com.nadoyagsa.pillaroid.entity.Appearance;
import com.nadoyagsa.pillaroid.entity.Medicine;
import com.nadoyagsa.pillaroid.repository.AppearanceRepository;
import com.nadoyagsa.pillaroid.repository.MedicineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicineExcelUtils {
	private final MedicineCrawlUtil medicineCrawlUtil;
	private final MedicineRepository medicineRepository;
	private final AppearanceRepository appearanceRepository;

	private final int TITLE_COL = 0;		// A열: 제품명
	private final int SERIALNUMBER_COL = 1;	// B열: 품목일련번호
	private final int STANDARDCODE_COL = 2;	// C열: 표준코드
	private final int APPEARANCE_COL = 3;	// D열: 외형정보
	private final int EFFICACY_COL = 4;		// E열: 효능효과
	private final int DOSAGE_COL = 5;		// F열: 용법용량
	private final int PRECAUTION_COL = 6;	// G열: 주의사항
	private final int INGREDIENT_COL = 7;	// H열: 성분정보
	private final int SAVE_COL = 8;			// I열: 저장방법

	public void updateMedicineExcel() throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/medicine.xlsx");

		if (inputStream == null)
			throw NotFoundException.MEDICINE_NOT_FOUND;

		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		int titleColIdx = 0;	// 의약품명이 적힌 열의 Idx

		for (int rowIdx = 1; rowIdx < rows; rowIdx++) {	//1, rows
			XSSFRow row = sheet.getRow(rowIdx);
			if (row != null) {
				// 엑셀 파일에서 제품명 읽기
				XSSFCell cell = row.getCell(titleColIdx);
				String itemName = cell.getStringCellValue();

				// NAVER 지식백과에서 제품명에 대한 최상위 결과 조회 (productLink, productName)
				Pair<String, String> productMeta = crawlProductLink(itemName);

				if (productMeta.getFirst() != null) {
					// 열 마지막에 크롤링한 제품명 넣기
					XSSFCell searchedProductName = row.createCell(11);
					searchedProductName.setCellValue(productMeta.getSecond());
					// 세부정보에서 원하는 항목 추출
					HashMap<Integer, String> result = crawlMedicineInfo(productMeta.getFirst());

					// 크롤링한 값을 셀에 저장
					result.forEach((colIdx, content) -> {
						if (content != null && content.length() > 32767) {	// 엑셀 값의 최대 문자 길이는 32767이기에 마지막 \n을 기준으로 수정함
							XSSFCell isTooLongValue = row.createCell(10);
							isTooLongValue.setCellValue(true);

							int canSetValueIndex = content.substring(0, 32768).lastIndexOf("\n");
							row.createCell(colIdx).setCellValue(content.substring(0, canSetValueIndex));
						}
						else
							row.createCell(colIdx).setCellValue(content);
					});
				}
			}
		}

		// 파일에 저장
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "/src/main/resources/data/medicine_result.xlsx";
		FileOutputStream outStream = new FileOutputStream(fileLocation);
		workbook.write(outStream);
		workbook.close();
	}

	private Pair<String, String> crawlProductLink(String itemName) throws IOException {
		String encodingItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8);
		String wikipediaUrl = "https://terms.naver.com/medicineSearch.naver?mode=nameSearch&query="+ encodingItemName;
		try {
			Document wikipediaDoc = Jsoup.connect(wikipediaUrl).get();

			Element firstElement = wikipediaDoc.selectFirst("ul[class=\"content_list\"]");
			if (firstElement == null) return Pair.create(null, null);   // 검색결과가 없으면 null 반환

			Element firstTitleElement = firstElement
					.selectFirst("li")
					.selectFirst("div[class=\"info_area\"]")
					.getElementsByClass("title").first()
					.selectFirst("a");
			String productLink = firstTitleElement.attr("href");
			String productName = firstTitleElement.text();
			return Pair.create(productLink, productName);
		} catch (HttpStatusException e){
			return Pair.create(null, null);   // 검색결과가 없으면 null 반환(404)
		}
	}

	private HashMap<Integer, String> crawlMedicineInfo(String productLink) throws JsonProcessingException {
		String detailBaseUrl = "https://terms.naver.com";
		String detailUrl = detailBaseUrl + productLink;

		MedicineCrawl medicineInfo = medicineCrawlUtil.getMedicineInfo(detailUrl);

		// (수정할 colIdx, content)로 된 hashMap
		HashMap<Integer, String> result = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		if (medicineInfo != null) {
			if (medicineInfo.getAppearanceInfo() != null) {
				String shapeString = objectMapper.writeValueAsString(medicineInfo.getAppearanceInfo());
				result.put(APPEARANCE_COL, shapeString);
			}
			result.put(EFFICACY_COL, medicineInfo.getEfficacy());
			result.put(DOSAGE_COL, medicineInfo.getDosage());
			result.put(PRECAUTION_COL, medicineInfo.getPrecaution());
			result.put(SAVE_COL, medicineInfo.getSave());
			result.put(INGREDIENT_COL, medicineInfo.getIngredient());
			return result;
		} else {
			log.error("medicineInfo 검색 결과 없음: " + detailUrl);
			throw NotFoundException.DATA_NOT_FOUND;
		}
	}

	// 엑셀 파일에 있는 medicine 정보를 DB에 저장하는 함수
	public void saveToDB(int startIdx, int endIdx) throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/medicine_save.xlsx");

		if (inputStream == null)
			throw NotFoundException.MEDICINE_NOT_FOUND;

		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet = workbook.getSheetAt(0);

		for (int rowIdx = startIdx; rowIdx < endIdx; rowIdx++) {
			XSSFRow row = sheet.getRow(rowIdx);
			if (row != null) {
				// Appearance 테이블에 저장
				// 외형정보 JSON->객체화
				String appearanceJsonString = row.getCell(APPEARANCE_COL).getStringCellValue();    // 현재 엑셀 파일의 외형정보에 빈 값 없으므로 null 검사 X
				ObjectMapper objectMapper = new ObjectMapper();
				Appearance appearance = objectMapper.readValue(appearanceJsonString, Appearance.class);
				appearanceRepository.save(appearance);

				// 엑셀 값이 null 인지 확인
				String standartCode = row.getCell(STANDARDCODE_COL) == null ? null : row.getCell(STANDARDCODE_COL).getStringCellValue();
				String efficacy = row.getCell(EFFICACY_COL) == null ? null : row.getCell(EFFICACY_COL).getStringCellValue();
				String dosage = row.getCell(DOSAGE_COL) == null ? null : row.getCell(DOSAGE_COL).getStringCellValue();
				String precaution = row.getCell(PRECAUTION_COL) == null ? null : row.getCell(PRECAUTION_COL).getStringCellValue();
				String ingredient = row.getCell(INGREDIENT_COL) == null ? null : row.getCell(INGREDIENT_COL).getStringCellValue();
				String save = row.getCell(SAVE_COL) == null ? null : row.getCell(SAVE_COL).getStringCellValue();

				// Medicine 테이블에 저장
				Medicine medicine = Medicine
						.builder()
						.name(row.getCell(TITLE_COL).getStringCellValue())
						.serialNumber(Integer.parseInt(row.getCell(SERIALNUMBER_COL).getStringCellValue()))
						.standardCode(standartCode)
						.appearance(appearance)
						.efficacy(efficacy)
						.dosage(dosage)
						.precaution(precaution)
						.ingredient(ingredient)
						.save(save)
						.build();
				medicineRepository.save(medicine);
			}
		}
	}
}
