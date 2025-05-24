import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {
    private static final Random random = new Random();

    public static void main(String[] args) {

        generateAndSaveInputData("input_data.txt");

        int[][] datasets = generateDatasets(50, 100, 200);
        writeResultsToExcel(datasets, "results.xlsx");

        ProductRatingSystem ratingSystem = new ProductRatingSystem(1000);

        for (int i = 1; i <= 100; i++) {
            int randomRating = 1 + random.nextInt(5);
            ratingSystem.updateRating(i, randomRating);
        }

        double avg = ratingSystem.getAverageRating(1, 20);
        System.out.println("Средний рейтинг товаров с ID 1-20: " + avg);

        try {
            ratingSystem.saveToFile("product_ratings.txt");
            System.out.println("Рейтинги товаров успешно сохранены в product_ratings.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateAndSaveInputData(String filename) {
        Random random = new Random();
        int startSize = 100;
        int endSize = 10000;
        int step = 200;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int size = startSize; size <= endSize; size += step) {
                int[] data = new int[size];
                for (int i = 0; i < size; i++) {
                    data[i] = random.nextInt(1000); // числа от 0 до 999
                }

                // Записываем строку: сначала размер, потом элементы через пробел
                writer.write(size + " ");
                for (int num : data) {
                    writer.write(num + " ");
                }
                writer.newLine();
            }

            System.out.println("Входные данные успешно записаны в файл " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[][] generateDatasets(int numSets, int startSize, int step) {
        int[][] datasets = new int[numSets][];
        for (int i = 0; i < numSets; i++) {
            int size = startSize + i * step;
            datasets[i] = new int[size];
            for (int j = 0; j < size; j++) {
                datasets[i][j] = 1 + random.nextInt(1000);
            }
        }
        return datasets;
    }

    private static void writeResultsToExcel(int[][] datasets, String filename) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // создаём три листа
            Sheet addSheet = workbook.createSheet("Добавление");
            Sheet querySheet = workbook.createSheet("Поиск");
            Sheet removeSheet = workbook.createSheet("Удаление");

            // заголовки для каждого листа
            createHeader(addSheet);
            createHeader(querySheet);
            createHeader(removeSheet);

            int addRowNum = 1;
            int queryRowNum = 1;
            int removeRowNum = 1;

            for (int i = 0; i < datasets.length; i++) {
                int[] data = datasets[i];
                FenwickTree tree = new FenwickTree(data.length);

                // добавление
                long startAdd = System.nanoTime();
                for (int j = 0; j < data.length; j++) {
                    tree.update(j + 1, data[j]);
                }
                addRow(addSheet, addRowNum++, data.length, "Добавление", System.nanoTime() - startAdd);

                // поиск
                long startQuery = System.nanoTime();
                tree.query(data.length);
                addRow(querySheet, queryRowNum++, data.length, "Поиск", System.nanoTime() - startQuery);

                // удаление
                long startRemove = System.nanoTime();
                for (int j = 0; j < data.length; j++) {
                    tree.remove(j + 1, data[j]);
                }
                addRow(removeSheet, removeRowNum++, data.length, "Удаление", System.nanoTime() - startRemove);
            }

            // Автоподбор ширины колонок для всех листов
            autoSizeColumns(addSheet);
            autoSizeColumns(querySheet);
            autoSizeColumns(removeSheet);

            try (FileOutputStream outputStream = new FileOutputStream(filename)) {
                workbook.write(outputStream);
                System.out.println("Файл " + filename + " успешно создан с тремя листами!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Количество данных");
        headerRow.createCell(1).setCellValue("Операция");
        headerRow.createCell(2).setCellValue("Время (секунды)");
    }

    private static void addRow(Sheet sheet, int rowNum, int size, String operation, long nanos) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(size);
        row.createCell(1).setCellValue(operation);
        row.createCell(2).setCellValue(nanos / 1_000_000_000.0);
    }

    private static void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}

