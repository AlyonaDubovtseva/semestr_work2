class FenwickTree {
    private int[] tree;

    public FenwickTree(int size) {
        tree = new int[size + 1];
    }

    public void update(int index, int value) {//Добавление / Обновление элемента
        while (index < tree.length) {
            tree[index] += value;
            index += index & -index;
        }
    }

    public int query(int index) { //Поиск (сумма на префиксе)
        int sum = 0;
        while (index > 0) {
            sum += tree[index];
            index -= index & -index;
        }
        return sum;
    }
    public int queryRange(int from, int to) {
        return query(to) - query(from - 1);
    }

    public void remove(int index, int value) {//Удаление
        update(index, -value);
    }
}
