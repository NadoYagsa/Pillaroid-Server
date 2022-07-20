package com.nadoyagsa.pillaroid.component;

import com.nadoyagsa.pillaroid.dto.AppearanceCrawl;
import com.nadoyagsa.pillaroid.dto.MedicineCrawl;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

@Component
public class MedicineCrawlUtil {
    public MedicineCrawl getMedicineInfo(String medicineUrl) {
        Connection conn = Jsoup.connect(medicineUrl);
        try {
            Document document = conn.get();

            Elements nameElements = document.getElementsByClass("stress");
            if (nameElements.size() > 0) {
                Element parent = nameElements.get(0).parent();
                // 네이버 의약품 검색 크롤링 중 <p></p>로 인해 잘린 부분이 있을 수 있어서 다음으로 변경
                String parentProcessed = parent.html()
                        .replace("</p>\n<p></p>", "")
                        .replace("<p></p>", "")
                        .replaceAll("<p\\s[^>]*>\\s*</p>", "");     //p 태그 중 속성값이 있는데 내용은 없는 컴포넌트 삭제

                Document documentProcessed = Jsoup.parse(parentProcessed);

                return getMedicineInfo(documentProcessed);
            }
            return getMedicineInfo(document);
        } catch (HttpStatusException e) {
            return MedicineCrawl.builder().build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public MedicineCrawl getMedicineInfo(Document document) {    // 지식백과에서의 의약품명 크롤링
        MedicineCrawl medicine = new MedicineCrawl();

        Elements nameElements = document.getElementsByClass("stress");
        for (Element nameElement : nameElements) {          // 외형정보, 성분정보, 저장방법, 효능효과, 용법용량, 사용상 주의사항
            String topic = nameElement.text();
            Element textElement = nameElement.nextElementSibling();

            if (textElement != null) {
                if (topic.equals("외형정보")) {
                    AppearanceCrawl appearanceInfo = new AppearanceCrawl();

                    String[] splitTopic = textElement.html()
                            .split("<strong>");         // 외형정보 안의 소주제를 나눔

                    for (String subTopic : splitTopic) {
                        String subText = subTopic
                                .replace("<br>", "\n")
                                .replaceAll("<[^>]*>", "").strip();

                        String[] information = subText.split(":", 2);

                        if (information.length <= 1)
                            continue;

                        if (information[0].contains("성상"))
                            appearanceInfo.setFeature(information[1].trim());
                        else if (information[0].contains("제형"))
                            appearanceInfo.setFormulation(information[1].trim());
                        else if (information[0].contains("모양"))
                            appearanceInfo.setShape(information[1].trim());
                        else if (information[0].contains("색상"))
                            appearanceInfo.setColor(information[1].trim());
                        else if (information[0].contains("분할선"))
                            appearanceInfo.setDividingLine(information[1].trim());
                        else if (information[0].contains("식별표기"))
                            appearanceInfo.setIdentificationMark(information[1].trim());
                    }
                    medicine.setAppearanceInfo(appearanceInfo);
                }
                else {
                    String text = textElement.html()
                            .replace("<br>", "\n")                      // 줄바꿈 모두 저장
                            .replaceAll("\\[허가사항변경[^]]*]\n*", "")   // [허가사항변경] 관련 불필요 내용 제거
                            .replaceAll("<[^>]*>", "")                  // 태그 모두 제거
                            .strip();

                    text = HtmlUtils.htmlUnescape(text);        // html 특수 문자 변환

                    switch (topic) {
                        case "성분정보":
                            medicine.setIngredient(text);
                            break;
                        case "저장방법":
                            medicine.setSave(text);
                            break;
                        case "효능효과":
                            medicine.setEfficacy(text);
                            break;
                        case "용법용량":
                            medicine.setDosage(text);
                            break;
                        case "사용상 주의사항":
                            medicine.setPrecaution(text);
                            break;
                    }
                }
            }
        }
        return medicine;
    }
}
