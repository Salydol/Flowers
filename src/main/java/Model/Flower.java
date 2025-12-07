package Model;

public class Flower {
    private int id;
    private String name;
    private int price;
    private String imagePath;

    public Flower(int id, String name, int price, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
    }

    public Flower(String name, int price, String imagePath) {
        this.id = -1;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
    }

    public Flower(String name, int price) {
        this.id = -1;
        this.name = name;
        this.price = price;
        this.imagePath = "";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return name + " (" + price + " ₸)";
    }
}
