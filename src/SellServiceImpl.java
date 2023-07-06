import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SellServiceImpl implements SellService{

    private final Map<String, Queue<String>> ticketingQueueMap = new HashMap<>();

    private final Object ticketingMonitor = new Object();

    public static final Object TASK_MONITOR = new Object();
    
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    private Map<String, Map<String, Boolean>> userTicketingResultMap = new HashMap<>();

    @PostConstruct
    public void ticketingTaskStart() {
        executorService.submit(() -> {
            while (true) {
                ticketingQueueMap.forEach((tripId, ticketingQueue) -> {
                    String userId = ticketingQueue.poll();
                    if (userId != null) {
                        //具体性能问题可以继续优化
                        synchronized (TASK_MONITOR) {
                            User user = new User();
                            //从数据库中读取用户余额
                            Trip trip = new Trip();
                            //从数据库/缓存中读取票价
                            Map<String, Boolean> ticketingResultMap = userTicketingResultMap.getOrDefault(userId, new HashMap<>());
                            userTicketingResultMap.put(userId, ticketingResultMap);
                            int compare = user.getBalance().compareTo(trip.getPrice());
                            if (compare < 0) {
                                ticketingResultMap.put(tripId, Boolean.FALSE);
                            } else {
                                //更新用户余额
                                ticketingResultMap.put(tripId, Boolean.TRUE);
                            }
                            TASK_MONITOR.notifyAll();
                        }
                    }
                });
            }
        });
    }
    @Override
    public int submitTicketing(String userId, String tripId) {
        int peopleNum = 0;
        synchronized (ticketingMonitor) {
            Queue<String> ticketingQueue = ticketingQueueMap.getOrDefault(tripId, new LinkedBlockingQueue<>());
            if (ticketingQueue.contains(userId)) {
                throw new RuntimeException("已在排队队列中");
            }
            peopleNum = ticketingQueue.size();
            ticketingQueue.add(userId);
            ticketingQueueMap.put(tripId, ticketingQueue);
        }
        return peopleNum;
    }

    @Override
    public void cancelTicketing(String userId, String tripId) {
        Queue<String> ticketingQueue = ticketingQueueMap.getOrDefault(tripId, new LinkedBlockingQueue<>());
        boolean result = ticketingQueue.remove(userId);
        if (!result) {
            throw new RuntimeException("当前用户未参与排队购票");
        }
    }

    @Override
    public Boolean getTicketingResult(String userId, String tripId) {
        return false;
    }
}