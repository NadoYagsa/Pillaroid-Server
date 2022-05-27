package com.nadoyagsa.pillaroid.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nadoyagsa.pillaroid.dto.PrescriptionResponse;
import com.nadoyagsa.pillaroid.dto.VoiceResponse;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.Medicine;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.util.HtmlUtils;

@Component
@RequiredArgsConstructor
public class MedicineExcelUtils {
	private final MedicineCrawlUtil medicineCrawlUtil;

	private final int TITLE_COL = 0;	// A열: 제품명
	private final int CODE_COL = 1;	// B열: 품목일련번호
	private final int SHAPE_COL = 2;	// C열: 외형정보(성상)
	private final int EFFICACY_COL = 4;	// E열: 효능효과
	private final int USAGE_COL = 5;	// F열: 용법용량
	private final int PRECAUTION_COL = 6;	// G열: 주의사항
	private final int SAVE_COL = 7;	// H열: 저장방법
	private final int INGREDIENT_COL = 9;	// J열: 성분정보

	public MedicineResponse findMedicineExcelByCode(Long code) throws IOException {
		ClassPathResource inputResource = new ClassPathResource("data/medicine.xlsx");

		FileInputStream file = new FileInputStream(new File(inputResource.getURI()));
		XSSFWorkbook workbook = new XSSFWorkbook(file);

		XSSFSheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();

		//품목일련번호 일치하는 데이터 찾기
		for (int rowIdx = 1; rowIdx<rows; rowIdx++) {
			XSSFRow row = sheet.getRow(rowIdx);
			if (row != null) {
				XSSFCell cell = row.getCell(CODE_COL);
				if (cell != null && cell.getCellType() == CellType.NUMERIC && String.valueOf(cell.getNumericCellValue()).equals(String.valueOf(code))) {
					return getMedicineResponse(row);
				} else if (cell != null && cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals(String.valueOf(code))) {
					return getMedicineResponse(row);
				}
			}
		}
		throw NotFoundException.MEDICINE_NOT_FOUND;
	}

	public MedicineResponse findMedicineExcelByName(String name) throws IOException {
		ClassPathResource inputResource = new ClassPathResource("data/medicine.xlsx");

		FileInputStream file = new FileInputStream(new File(inputResource.getURI()));
		XSSFWorkbook workbook = new XSSFWorkbook(file);

		XSSFSheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();

		//제품명 일치하는 데이터 찾기
		for (int rowIdx = 1; rowIdx<rows; rowIdx++) {
			XSSFRow row = sheet.getRow(rowIdx);
			if (row != null) {
				XSSFCell cell = row.getCell(TITLE_COL);
				String title = cell.getStringCellValue();
				String extractedTitle = title.substring(0, Math.min(name.length(), title.length()));
				if (extractedTitle.equals(name)) {
					return getMedicineResponse(row);
				}
			}
		}
		throw NotFoundException.MEDICINE_NOT_FOUND;
	}

	private MedicineResponse getMedicineResponse(XSSFRow row) {
		XSSFCell cell = row.getCell(CODE_COL);
		CellType cellType = cell.getCellType();

		if (cellType == CellType.NUMERIC)
			return MedicineResponse.builder()
					.idx(Double.valueOf(row.getCell(CODE_COL).getNumericCellValue()).longValue())
					.code(Double.valueOf(row.getCell(CODE_COL).getNumericCellValue()).longValue())
					.name(row.getCell(TITLE_COL).getStringCellValue())
					.appearanceInfo(row.getCell(SHAPE_COL).getStringCellValue())
					.ingredient(row.getCell(INGREDIENT_COL).getStringCellValue())
					.save(row.getCell(SAVE_COL).getStringCellValue())
					.efficacy(row.getCell(EFFICACY_COL).getStringCellValue())
					.usage(row.getCell(USAGE_COL).getStringCellValue())
					.precautions(HtmlUtils.htmlUnescape(row.getCell(PRECAUTION_COL).getStringCellValue()))
					.build();
		else if (cellType == CellType.STRING)
			return MedicineResponse.builder()
					.idx(Long.valueOf(row.getCell(CODE_COL).getStringCellValue()))
					.code(Long.valueOf(row.getCell(CODE_COL).getStringCellValue()))
					.name(row.getCell(TITLE_COL).getStringCellValue())
					.appearanceInfo(row.getCell(SHAPE_COL).getStringCellValue())
					.ingredient(row.getCell(INGREDIENT_COL).getStringCellValue())
					.save(row.getCell(SAVE_COL).getStringCellValue())
					.efficacy(row.getCell(EFFICACY_COL).getStringCellValue())
					.usage(row.getCell(USAGE_COL).getStringCellValue())
					.precautions(HtmlUtils.htmlUnescape(row.getCell(PRECAUTION_COL).getStringCellValue()))
					.build();

		throw NotFoundException.MEDICINE_NOT_FOUND;
	}

	public List<VoiceResponse> findVoiceMedicineListByName(String name) throws IOException {
		List<VoiceResponse> medicineList = new ArrayList<>();

		ClassPathResource inputResource = new ClassPathResource("data/medicine.xlsx");

		FileInputStream file = new FileInputStream(new File(inputResource.getURI()));
		XSSFWorkbook workbook = new XSSFWorkbook(file);

		XSSFSheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();

		for (int rowIdx=1; rowIdx<rows; rowIdx++) {
			XSSFRow row = sheet.getRow(rowIdx);
			if (row != null) {
				XSSFCell titleCell = row.getCell(TITLE_COL);
				String title = titleCell.getStringCellValue();
				
				// 음성 검색 명이 포함이 되어있는지 확인
				if (title.contains(name) && row.getCell(CODE_COL) != null) {
					XSSFCell codeCell = row.getCell(CODE_COL);
					CellType cellType = codeCell.getCellType();

					Long code = 0L;
					if (cellType == CellType.NUMERIC)
						code = Double.valueOf(codeCell.getNumericCellValue()).longValue();
					else if (cellType == CellType.STRING)
						code = Long.valueOf(codeCell.getStringCellValue());

					medicineList.add(VoiceResponse.builder()
							.idx(code)	//TODO: DB에 들어가면 ID로 바뀌어야 함!
							.name(title)
							.build());
				}
			}
		}
		
		return medicineList;
	}

	public List<PrescriptionResponse> findPrescriptionMedicineListByName(String[] nameList) throws IOException {
		List<PrescriptionResponse> medicineList = new ArrayList<>();

		ClassPathResource inputResource = new ClassPathResource("data/medicine.xlsx");

		FileInputStream file = new FileInputStream(new File(inputResource.getURI()));
		XSSFWorkbook workbook = new XSSFWorkbook(file);

		XSSFSheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();

		ArrayList<String> tempNameList = new ArrayList<>(List.of(nameList));
		//제품명 일치하는 데이터 찾기
		for (int rowIdx = 1; rowIdx<rows; rowIdx++) {
			XSSFRow row = sheet.getRow(rowIdx);
			if (row != null) {
				XSSFCell cell = row.getCell(TITLE_COL);
				String title = cell.getStringCellValue();

				for (int i=0; i<tempNameList.size(); i++) {
					String extractedTitle = title.substring(0, Math.min(tempNameList.get(i).length(), title.length()));
					if (extractedTitle.equals(tempNameList.get(i)) && row.getCell(CODE_COL) != null) {
						tempNameList.remove(i);

						XSSFCell codeCell = row.getCell(CODE_COL);
						CellType cellType = codeCell.getCellType();

						Long code = 0L;
						if (cellType == CellType.NUMERIC)
							code = Double.valueOf(codeCell.getNumericCellValue()).longValue();
						else if (cellType == CellType.STRING)
							code = Long.valueOf(codeCell.getStringCellValue());

						medicineList.add(PrescriptionResponse.builder()
								.idx(code)
								.name(title)
								.appearanceInfo(row.getCell(SHAPE_COL).getStringCellValue())
								.efficacy(row.getCell(EFFICACY_COL).getStringCellValue())
								.usage(row.getCell(USAGE_COL).getStringCellValue())
								.build());
						break;
					}
				}
			}

			if (tempNameList.size() == 0)
				return medicineList;
		}

		return medicineList;
	}

	public void updateMedicineExcel() throws IOException {
		ClassPathResource inputResource = new ClassPathResource("data/medicine.xlsx");

		FileInputStream file = new FileInputStream(new File(inputResource.getURI()));
		XSSFWorkbook workbook = new XSSFWorkbook(file);

		XSSFSheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		int titleColIdx = 0;	//의약품명이 적힌 열의 Idx

		for (int rowIdx = 1; rowIdx<rows; rowIdx++) {	//1, rows
			XSSFRow row = sheet.getRow(rowIdx);
			if (row != null) {
				//엑셀 파일에서 제품명 읽기
				XSSFCell cell = row.getCell(titleColIdx);
				String itemName = cell.getStringCellValue();

				//NAVER 지식백과에서 제품명에 대한 최상위 결과 조회 (productLink, productName)
				Pair<String, String> productMeta = crawlProductLink(itemName);

				if (productMeta.getFirst() != null) {
					//열 마지막에 크롤링한 제품명 넣기
					XSSFCell searchedProductName = row.createCell(9);
					searchedProductName.setCellValue(productMeta.getSecond());
					//세부정보에서 원하는 항목 추출
					HashMap<Integer, String> result = crawlMedicineInfo(productMeta.getFirst());

					//크롤링한 값을 셀에 저장
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
				} else {
					//값이 없으면 공백 저장
					for (int colIdx = 2; colIdx <= 9; colIdx++) {   //2:성상, 4: 효능효과, 5: 용법용량, 6: 주의사항, 7: 저장방법, 9: 성분정보
						row.createCell(colIdx).setCellValue("");
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

	private Pair<String, String> crawlProductLink(String itemName) throws IOException {
		String encodingItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8);
		String wikipediaUrl = "https://terms.naver.com/medicineSearch.naver?mode=nameSearch&query="+ encodingItemName;
		try {
			Document wikipediaDoc = Jsoup.connect(wikipediaUrl).get();

			Element firstElement = wikipediaDoc.selectFirst("ul[class=\"content_list\"]");
			if (firstElement == null) return Pair.create(null, null);   //검색결과가 없으면 null 반환

			Element firstTitleElement = firstElement
					.selectFirst("li")
					.selectFirst("div[class=\"info_area\"]")
					.getElementsByClass("title").first()
					.selectFirst("a");
			String productLink = firstTitleElement.attr("href");
			String productName = firstTitleElement.text();
			return Pair.create(productLink, productName);
		} catch (HttpStatusException e){
			return Pair.create(null, null);   //검색결과가 없으면 null 반환(404)
		}
	}

	private HashMap<Integer, String> crawlMedicineInfo(String productLink) {
		String detailBaseUrl = "https://terms.naver.com";
		String detailUrl = detailBaseUrl + productLink;

		Medicine medicineInfo = medicineCrawlUtil.getMedicineInfo(detailUrl);

		//(수정할 colIdx, content)로 된 hashMap
		HashMap<Integer, String> result = new HashMap<>();
		result.put(SHAPE_COL, medicineInfo.getAppearanceInfo().toString());
		result.put(EFFICACY_COL, medicineInfo.getEfficacy());
		result.put(USAGE_COL, medicineInfo.getUsage());
		result.put(PRECAUTION_COL, medicineInfo.getPrecautions());
		result.put(SAVE_COL, medicineInfo.getSave());
		result.put(INGREDIENT_COL, medicineInfo.getIngredient());
		return result;
	}
}
