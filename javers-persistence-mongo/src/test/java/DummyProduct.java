import javax.persistence.Id;

public class DummyProduct {

    @Id
    private int id;
    private String name;

    public DummyProduct(int id, String name) {
        this.id = id;
        this.name = name;
    }


}
