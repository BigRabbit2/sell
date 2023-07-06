public class Test {

    private static final SellService sellService = new SellServiceImpl();

    public static void main(String[] args) {

        //根据前端交互提供参数
        User user = new User();
        Trip trip = new Trip();

        int peopleNum = sellService.submitTicketing(user.getUserId(), trip.getTripId());
        System.out.println("当前前方排队人数:" + peopleNum);
        Boolean ticketingResult = null;
        while (ticketingResult == null) {
            ticketingResult = sellService.getTicketingResult(user.getUserId(), trip.getTripId());
            if (ticketingResult == null) {
                try {
                    SellServiceImpl.TASK_MONITOR_MAP.get(trip.getTripId()).wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("购票结果:" + ticketingResult.toString());
    }
}