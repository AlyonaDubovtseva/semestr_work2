import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ProductRatingSystem {
    private FenwickTree ratingTree;
    private FenwickTree countTree;
    private Map<Integer, Integer> productRatings; // ID товара → рейтинг

    public ProductRatingSystem(int maxProducts) {
        ratingTree = new FenwickTree(maxProducts);
        countTree = new FenwickTree(maxProducts);
        productRatings = new HashMap<>();
    }

    // 1. Добавление/обновление рейтинга
    public void updateRating(int productId, int rating) {
        if (productRatings.containsKey(productId)) {
            int oldRating = productRatings.get(productId);
            ratingTree.remove(productId, oldRating);
            countTree.remove(productId, 1);
        }
        ratingTree.update(productId, rating);
        countTree.update(productId, 1);
        productRatings.put(productId, rating);
    }

    // 2. Удаление товара
    public void removeProduct(int productId) {
        if (productRatings.containsKey(productId)) {
            int rating = productRatings.get(productId);
            ratingTree.remove(productId, rating);
            countTree.remove(productId, 1);
            productRatings.remove(productId);
        }
    }

    // 3. Средний рейтинг для диапазона товаров
    public double getAverageRating(int fromId, int toId) {
        int totalRating = ratingTree.queryRange(fromId, toId);
        int count = countTree.queryRange(fromId, toId);
        return count == 0 ? 0 : (double) totalRating / count;
    }

    // 4. Сохранение данных в файл
    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println("ID товара\tРейтинг");
            for (Map.Entry<Integer, Integer> entry : productRatings.entrySet()) {
                writer.println(entry.getKey() + "\t" + entry.getValue());
            }
        }
    }
}
