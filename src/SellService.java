public interface SellService {

    int submitTicketing(String userId, String tripId);

    void cancelTicketing(String userId, String tripId);

    Boolean getTicketingResult(String userId, String tripId);

}
