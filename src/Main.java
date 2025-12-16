import java.util.ArrayDeque;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    // room piles (stacks)
    static ArrayDeque<Room> singleRooms = new ArrayDeque<>();
    static ArrayDeque<Room> doubleRooms = new ArrayDeque<>();
    static ArrayDeque<Room> suiteRooms = new ArrayDeque<>();
    static ArrayDeque<Room> deluxeRooms = new ArrayDeque<>();

    // customers queue (waiting reservations)
    static ArrayDeque<Reservation> singleQueue = new ArrayDeque<>();
    static ArrayDeque<Reservation> doubleQueue = new ArrayDeque<>();
    static ArrayDeque<Reservation> suiteQueue = new ArrayDeque<>();
    static ArrayDeque<Reservation> deluxeQueue = new ArrayDeque<>();

    // booked rooms list
    static ArrayList<Room> bookedRooms = new ArrayList<>();

    public static void main(String[] args) {
        initializeRooms();
        System.out.println("=== BEFORE processing reservations ===");
        printRoomPile("Single pile", singleRooms);
        printRoomPile("Double pile", doubleRooms);
        printRoomPile("Suite pile", suiteRooms);
        printRoomPile("Deluxe pile", deluxeRooms);

        try {
            BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"));

            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int reservationId = Integer.parseInt(parts[0].trim());
                String customerName = parts[1].trim();
                RoomType roomType = RoomType.valueOf(parts[2].trim().toUpperCase());
                Reservation reservation = new Reservation(reservationId, customerName, roomType);
                processReservation(reservation);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("=====================================");
        System.out.println("=== AFTER processing reservations ===");
        printRoomPile("Single pile", singleRooms);
        printRoomPile("Double pile", doubleRooms);
        printRoomPile("Suite pile", suiteRooms);
        printRoomPile("Deluxe pile", deluxeRooms);

        printReservationQueue("Single waiting line", singleQueue);
        printReservationQueue("Double waiting line", doubleQueue);
        printReservationQueue("Suite waiting line", suiteQueue);
        printReservationQueue("Deluxe waiting line", deluxeQueue);
        System.out.println("=====================================");

        System.out.println("Booked rooms: " + bookedRooms.size());
        System.out.println("Single waiting: " + singleQueue.size());
        System.out.println("Double waiting: " + doubleQueue.size());
        System.out.println("Suite waiting: " + suiteQueue.size());
        System.out.println("Deluxe waiting: " + deluxeQueue.size());

        freeOddNumberedRooms();

        System.out.println("=====================================");
        System.out.println("=== AFTER making odd numbered rooms free ===");
        printRoomPile("Single pile", singleRooms);
        printRoomPile("Double pile", doubleRooms);
        printRoomPile("Suite pile", suiteRooms);
        printRoomPile("Deluxe pile", deluxeRooms);
        printReservationQueue("Single waiting line", singleQueue);
        printReservationQueue("Double waiting line", doubleQueue);
        printReservationQueue("Suite waiting line", suiteQueue);
        printReservationQueue("Deluxe waiting line", deluxeQueue);
        System.out.println("=====================================");

        processWaitingReservations();

        System.out.println("=====================================");
        System.out.println("=== AFTER processing waiting lines ===");
        printReservationQueue("Single waiting line", singleQueue);
        printReservationQueue("Double waiting line", doubleQueue);
        printReservationQueue("Suite waiting line", suiteQueue);
        printReservationQueue("Deluxe waiting line", deluxeQueue);
        System.out.println("=====================================");

        printFinalRoomLists(); 


    }

    // methods
    static void initializeRooms() {
        for (int i = 5; i >= 1; i--) {
            singleRooms.push(new Room(RoomType.SINGLE, i, true));
        }

        for (int i = 5; i >= 1; i--) {
            doubleRooms.push(new Room(RoomType.DOUBLE, i + 5, true));
        }

        for (int i = 5; i >= 1; i--) {
            suiteRooms.push(new Room(RoomType.SUITE, i + 10, true));
        }

        for (int i = 5; i >= 1; i--) {
            deluxeRooms.push(new Room(RoomType.DELUXE, i + 15, true));
        }
    }

    static ArrayDeque<Room> getRoomStack(RoomType roomType) {
        switch (roomType) {
            case SINGLE:
                return singleRooms;
            case DOUBLE:
                return doubleRooms;
            case SUITE:
                return suiteRooms;
            case DELUXE:
                return deluxeRooms;
            default:
                throw new IllegalArgumentException("Unknown room type: " + roomType);
        }
    }

    static ArrayDeque<Reservation> getReservationQueue(RoomType roomType) {
        switch (roomType) {
            case SINGLE:
                return singleQueue;
            case DOUBLE:
                return doubleQueue;
            case SUITE:
                return suiteQueue;
            case DELUXE:
                return deluxeQueue;
            default:
                throw new IllegalArgumentException("Unknown room type: " + roomType);
        }
    }

    static void processReservation(Reservation reservation) {
        ArrayDeque<Room> roomStack = getRoomStack(reservation.getRoomType());
        ArrayDeque<Reservation> reservationQueue = getReservationQueue(reservation.getRoomType());

        if (!roomStack.isEmpty()) {
            Room room = roomStack.pop();
            room.setAvailable(false);
            bookedRooms.add(room);
            System.out.println("Reservation " + reservation.getReservationId() + " for " + 
            reservation.getCustomerName() + " has been booked in room " + room.getRoomNumber());
        } else {
            reservationQueue.offer(reservation);
            System.out.println("No available rooms for reservation " + 
            reservation.getReservationId() + ". Added to waiting list.");
        }
    }

    static void freeOddNumberedRooms() {
        ArrayList<Room> roomsToFree = new ArrayList<>();
        for (Room room : bookedRooms) {
            if (room.getRoomNumber() % 2 != 0) {
                roomsToFree.add(room);
            }
        }

        roomsToFree.sort((r1, r2) -> r2.getRoomNumber() - r1.getRoomNumber());

        for (Room room : roomsToFree) {
            bookedRooms.remove(room);
            room.setAvailable(true);
            getRoomStack(room.getRoomType()).push(room);
            System.out.println("Freed room " + room.getRoomNumber());
        }
    }

    static void processWaitingReservations() {
        for (RoomType roomType : RoomType.values()) {
            ArrayDeque<Room> roomStack = getRoomStack(roomType);
            ArrayDeque<Reservation> reservationQueue = getReservationQueue(roomType);

            while (!roomStack.isEmpty() && !reservationQueue.isEmpty()) {
                Reservation reservation = reservationQueue.poll();
                Room room = roomStack.pop();

                room.setAvailable(false);
                bookedRooms.add(room);

                System.out.println(
                    "Processed waiting reservation " + reservation.getReservationId() +
                    " for " + reservation.getCustomerName() +
                    " in room " + room.getRoomNumber()
                );
            }
        }
    }

    static void printRoomPile(String title, ArrayDeque<Room> pile) {
        System.out.println(title);
        for (Room r : pile) { 
                System.out.println("  Room " + r.getRoomNumber() + " - " + r.getRoomType() + " - available=" + r.isAvailable());
            }
    }

    static void printReservationQueue(String title, ArrayDeque<Reservation> queue) {
        System.out.println(title);
        for (Reservation r : queue) { 
                System.out.println("  " + r.getReservationId() + " - " + r.getCustomerName() + " - " + r.getRoomType());
            }
    }

    static void printFinalRoomLists() {
        System.out.println("***********************************************");
        System.out.println("Unavailable Rooms");
        System.out.println("***********************************************");

        // copy + sort booked rooms
        ArrayList<Room> unavailable = new ArrayList<>(bookedRooms);
        unavailable.sort((r1, r2) -> r1.getRoomNumber() - r2.getRoomNumber());

        for (Room r : unavailable) {
            System.out.println("Room " + r.getRoomNumber() + " - " + r.getRoomType());
        }

        System.out.println("***********************************************");
        System.out.println("Available Rooms");
        System.out.println("***********************************************");

        // collect all available rooms
        ArrayList<Room> available = new ArrayList<>();
        available.addAll(singleRooms);
        available.addAll(doubleRooms);
        available.addAll(suiteRooms);
        available.addAll(deluxeRooms);

        // sort available rooms
        available.sort((r1, r2) -> r1.getRoomNumber() - r2.getRoomNumber());

        for (Room r : available) {
            System.out.println("Room " + r.getRoomNumber() + " - " + r.getRoomType());
        }
    }

}






