package service;

import Model.BouquetItem;
import Model.Packaging;

import java.util.List;

public class BouquetCalculator {

    public static int calculateTotal(List<BouquetItem> items, Packaging packaging) {
        int total = 0;

        // 1. считаем цветы
        for (BouquetItem item : items) {
            total += item.getFlower().getPrice() * item.getQuantity();
        }

        // 2. упаковка
        if (packaging != null) {
            total += packaging.getPrice();
        }

        return total;
    }
}
