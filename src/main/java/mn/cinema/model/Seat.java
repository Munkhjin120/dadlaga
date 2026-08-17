package mn.cinema.model;

public class Seat {
    private int id;
    private int hallId;
    private String rowLabel;
    private int seatNumber;
    private boolean booked; // тухайн тоглолтод захиалагдсан эсэх (query үед бөглөгдөнө)

    public Seat() {}

    public Seat(int id, int hallId, String rowLabel, int seatNumber) {
        this.id = id;
        this.hallId = hallId;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getHallId() { return hallId; }
    public void setHallId(int hallId) { this.hallId = hallId; }
    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }

    public String getLabel() { return rowLabel + seatNumber; }
}
