package mn.cinema.model;

public class Hall {
    private int id;
    private String name;
    private int rowsCount;
    private int colsCount;

    public Hall() {}

    public Hall(int id, String name, int rowsCount, int colsCount) {
        this.id = id;
        this.name = name;
        this.rowsCount = rowsCount;
        this.colsCount = colsCount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getRowsCount() { return rowsCount; }
    public void setRowsCount(int rowsCount) { this.rowsCount = rowsCount; }
    public int getColsCount() { return colsCount; }
    public void setColsCount(int colsCount) { this.colsCount = colsCount; }

    public int getSeatCount() { return rowsCount * colsCount; }

    @Override
    public String toString() { return name + " (" + getSeatCount() + " суудал)"; }
}
