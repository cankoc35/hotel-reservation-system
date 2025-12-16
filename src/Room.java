public class Room {
    // attributes
    private RoomType roomType;
    private int roomNumber;
    private boolean isAvailable;

    // constructor
    public Room(RoomType roomType, int roomNumber, boolean isAvailable) {
        this.roomType = roomType;
        this.roomNumber = roomNumber;
        this.isAvailable = isAvailable;
    }

    // getters and setters
    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}