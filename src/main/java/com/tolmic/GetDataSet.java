package com.tolmic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tolmic.entity.Vacancy;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class GetDataSet {

    private static String accessToken = "USERMGF5IEV2JIJGR4GQ84A3MGQQ6HCEISSRCIAINUAQ9U0NDNI31S9E5BJLF9I1";

    private static ObjectMapper objectMapper;

    private static RestTemplate restTemplate = new RestTemplate();

    private static String httpLink = "https://api.hh.ru/vacancies";

    private static JsonNode getResponseEntityMain(String parameters, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            String.format(httpLink + parameters),
                HttpMethod.GET, entity, JsonNode.class);

        return response.getBody();
    }

    //#region get vacancies by name
    private static JsonNode getResponseEntityByName(String parameters) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        return getResponseEntityMain(parameters, headers);
    }

    private static JsonNode getFirstPageResponse(String text) throws JsonProcessingException {
        return getResponseEntityByName(String.format("?text=%s&page=%s", text, 0));
    }

    private static JsonNode getResponseByPage(String text, int page) throws JsonProcessingException {
        return getResponseEntityByName(String.format("?text=%s&page=%s", text, page));
    }
    //#endregion

    private static JsonNode getResponseEntityById(String parameters) {
        return getResponseEntityMain(parameters, new HttpHeaders());
    }

    private static List<String> getVacanciesIdentifiers(JsonNode jsonNode) throws JsonProcessingException {
        JsonNode jn = jsonNode.get("items");
        
        List<String> identifiers = new ArrayList<>();
        for (JsonNode node : jn) {
            identifiers.add(node.get("id").asText());
        }

        return identifiers;
    }

    private static List<Vacancy> getVacanciesByIdentifiers(List<String> identifiers) throws JsonProcessingException {
        List<Vacancy> vacancies = new ArrayList<>();

        for (String id : identifiers) {
            boolean f = true;

            while (f) {
                f = false;

                try {
                    JsonNode jsonNode = getResponseEntityById("/" + id);
                    Vacancy vacancy = getVacancyFromJson(jsonNode);
    
                    vacancies.add(vacancy);
                } catch (Exception e) {
                    String error = e.getMessage();
    
                    if (error.contains("Forbidden")) {
                        f = true;

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException inter) {

                        }
                    }
    
                }
            }
        }

        return vacancies;
    }

    private static Vacancy getVacancyFromJson(JsonNode jsonNode) throws JsonProcessingException {
        return objectMapper.readValue(jsonNode.toString(), Vacancy.class);
    }

    //#region clean data
    private static String deleteTegs(String text) {
        String text1 = text;

        int ind1 = text1.indexOf("<");
        int ind2 = text1.indexOf(">");

        while (ind1 > -1) {
            text1 = text1.substring(0, ind1) + text1.substring(ind2 + 1, text1.length());
            
            ind1 = text1.indexOf("<");
            ind2 = text1.indexOf(">");
        }

        return text1;
    }

    private static String deleteExtraSpace(String text) {
        StringBuilder sb = new StringBuilder("");

        int n = text.length();
        boolean prevIsSpace = false;
        for (int i = 0; i < n; i++) {
            String symb = String.valueOf(text.charAt(i));

            if (symb.equals(" ") && prevIsSpace) {
                continue;
            }

            sb.append(symb);

            prevIsSpace = symb.equals(" ");
        }

        return sb.toString();
    }

    private static void cleanFromTegs(List<Vacancy> vacancies) {
        int n = vacancies.size();

        for (int i = 0; i < n; i++) {
            Vacancy v = vacancies.get(i);

            if (v.getId() == 118012792) {
                int a = 10;
            }

            String description = deleteTegs(v.getDescription());
            description = deleteExtraSpace(description);

            v.setDescription(description);
        }
    }
    //#endregion
    
    private static List<Vacancy> getVacancies(JsonNode jsonNode) throws JsonProcessingException {
        List<String> identifiers = getVacanciesIdentifiers(jsonNode);
        List<Vacancy> vacancies = getVacanciesByIdentifiers(identifiers);

        cleanFromTegs(vacancies);

        return vacancies;
    }


    //#region record data to excel
    private static void fillInLine(XSSFRow row, Vacancy v) {
        row.createCell(0).setCellValue(v.getId());
        row.createCell(1).setCellValue(v.getName());
        row.createCell(2).setCellValue(v.getDescription());
        row.createCell(3).setCellValue(v.getExperience());
        row.createCell(4).setCellValue(v.getEmployer());
        row.createCell(5).setCellValue(v.getCity());
    }

    private static void writeToExcel(List<Vacancy> vacancies) throws IOException {
        String path = (new File("").getAbsolutePath()) + "/src/main/resources/dataset.xlsx";

        FileInputStream fis = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        XSSFSheet sheet = workbook.getSheetAt(0);
        
        int lastRowNum = sheet.getLastRowNum();

        int n = vacancies.size();
        for (int i = 0; i < n; i++) {
            XSSFRow newRow = sheet.createRow(lastRowNum + i + 1);
        
            fillInLine(newRow, vacancies.get(i));
        }
        
        fis.close();
        
        FileOutputStream fos = new FileOutputStream(path);
        workbook.write(fos);
        fos.close();
    }

    private static void writeIdesToExcel(List<String> ides) throws IOException {
        String path = (new File("").getAbsolutePath()) + "/src/main/resources/ides.xlsx";

        FileInputStream fis = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        XSSFSheet sheet = workbook.getSheetAt(0);
        
        int lastRowNum = sheet.getLastRowNum();

        int n = ides.size();
        for (int i = 0; i < n; i++) {
            XSSFRow newRow = sheet.createRow(lastRowNum + i + 1);
        
            newRow.createCell(0).setCellValue(ides.get(i));
        }
        
        fis.close();
        
        FileOutputStream fos = new FileOutputStream(path);
        workbook.write(fos);
        fos.close();
    }

    private static void writeToExcelMain(List<Vacancy> vacancies) {
        boolean f = true;
        while (f) {
            f = false;

            try {
                writeToExcel(vacancies);
            } catch (IOException e) {
                f = true;
            }
        }
    }

    private static void writeToExcelIdesMain(List<String> ides) {
        boolean f = true;
        while (f) {
            f = false;

            try {
                writeIdesToExcel(ides);
            } catch (IOException e) {
                f = true;
            }
        }
    }
    
    private static List<String> readIdes() {
        List<String> ides = new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream((new File("").getAbsolutePath()) + "/src/main/resources/ides.xlsx");
            Workbook workbook = new XSSFWorkbook(fis);
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                DataFormatter dataFormatter = new DataFormatter();
                ides.add(dataFormatter.formatCellValue(cellIterator.next()));
            }

            workbook.close();
        } catch (IOException e) {

        }

        return ides;
    }
    //#endregion

    public static void main2(String[] args) throws JsonProcessingException, FileNotFoundException {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());

        String[] vacancyNames = new String[] {"Бухгалтер", "Системный администратор", "Разработчик", "Графический дизайнер",
                    "Повар", "Врач", "Кассир", "Продавец", "Уборщик", "Программист", "Инженер", "Учитель", "Преподаватель",
                    "Грузчик", "Сварщик", "Контроллер", "Делопроизводитель", "Менеджер", "Охранник", "Аналитик", "Экономист",
                    "Водитель", "Оператор", "Администратор"};
        
        for (String vacancyName : vacancyNames) {
            System.out.println(vacancyName);

            // PrintStream fileOut = new PrintStream(new File("C:\\Users\\dtolm\\OneDrive\\Рабочий стол\\данные.txt"));
            // System.setOut(fileOut);

            List<String> ides = new ArrayList<>();

            JsonNode jsonNode1 = getFirstPageResponse(vacancyName);
            int pageNumber = jsonNode1.get("pages").asInt();
            ides.addAll(getVacanciesIdentifiers(jsonNode1));

            for (int i = 1; i < pageNumber; i++) {
                ides.addAll(getVacanciesIdentifiers(getResponseByPage(vacancyName, i)));
            }

            writeToExcelIdesMain(ides);
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());

        List<String> ides = readIdes();

        double n = ides.size();

        int plank = 0;
        for (double i = 12854; i < n; i++) {
            List<Vacancy> v = getVacanciesByIdentifiers(List.of(ides.get((int) i)));

            cleanFromTegs(v);

            writeToExcelMain(v);

            int pers = (int) (((i + 1) / n) * 100);
            if (pers > plank) {
                System.out.println("Is ready on: " + pers + " %");
                plank = pers;
            } 
        }
    }

    public static void main1(String[] args) {
        List<String> ides = readIdes();

        Set<String> map = new TreeSet<>();

        int repeats = 0;

        for (String id : ides) {
            if (map.contains(id)) {
                repeats += 1;
            }

            map.add(id);
        }

        writeToExcelIdesMain(map.stream().collect(Collectors.toList()));

        System.out.println("Repeats was: " + repeats);
    }

}
