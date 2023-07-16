public class TicketGrabbingDemo {
    public static void main(String[] args) {
        TicketMachine ticketMachine = new TicketMachine(100, 30);
        ticketMachine.startGrabTicket();
    }
}
