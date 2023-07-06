import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SellServiceImpl implements SellService{

    private final Map<String, Queue<String>> ticketingQueueMap = new HashMap<>();

    private final Map<String, Object> ticketingMonitorMap = new HashMap<>();

    public static final Map<String, Object> TASK_MONITOR_MAP = new HashMap<>();
    
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    private Map<String, Map<String, Boolean>> userTicketingResultMap = new HashMap<>();

    @PostConstruct
    public void ticketingTaskStart() {

        //获取当天所有车次给每个车次分配一个Object对象用来上锁
        //可以用分布式锁替代，但是Test中等待购票结果需要换种通知方式
        //多节点情况下需要用分布式锁
        initTaskMonitor();

        executorService.submit(() -> {
            while (true) {
                ticketingQueueMap.forEach((tripId, ticketingQueue) -> {
                    Object taskMonitor = TASK_MONITOR_MAP.get(tripId);
                    String userId = ticketingQueue.poll();
                    if (userId != null) {
                        Map<String, Boolean> ticketingResultMap = new HashMap<>();
                        Map<String, Boolean> putResult = userTicketingResultMap.putIfAbsent(userId, ticketingResultMap);
                        if (putResult != null) {
                            ticketingResultMap = putResult;
                        }
                        ticketingResultMap.put(tripId, Boolean.FALSE);
                        if (!checkIfTicketingAgain(tripId, userId)) {
                            synchronized (taskMonitor) {
                                Trip trip = getTripInfo(tripId);
                                if (trip.getCount() > 0) {
                                    //从数据库中读取用户余额
                                    User user = new User();
                                    BigDecimal newBalance = user.getBalance().subtract(trip.getPrice());
                                    if (newBalance.intValue() < 0) {
                                        ticketingResultMap.put(tripId, Boolean.FALSE);
                                    } else {
                                        updateBalance(userId, newBalance);
                                        ticketingResultMap.put(tripId, Boolean.TRUE);
                                        //加入缓存 tripId:xxxx_userId:xxxx 过期时间为距离开车时间的间隔

                                        updateTripInfo(tripId);

                                    }
                                }
                            }
                        }
                    }
                    taskMonitor.notifyAll();
                });
            }
        });
    }

    /**
     * 查询余票信息
     * @param tripId
     * @return
     */
    private Trip getTripInfo(String tripId) {
        //返回余票信息 可以从数据库中获取 或者从缓存中获取
        return new Trip();
    }

    /**
     * 查询是否多次购买
     */
    private Boolean checkIfTicketingAgain(String tripId, String userId) {
        //缓存key tripId:xxxx_userId:xxxx是否存在
        return Boolean.FALSE;
    }

    /**
     * 更新余额
     * @param userId
     * @param newBalance
     * @return
     */
    private Boolean updateBalance(String userId, BigDecimal newBalance) {
        return Boolean.TRUE;
    }

    /**
     * 更新余票信息
     * @param tripId
     */
    private void updateTripInfo(String tripId) {

    }

    private void initTaskMonitor() {

    }
    @Override
    public int submitTicketing(String userId, String tripId) {
        int peopleNum = 0;
        Object ticketingMonitor = ticketingMonitorMap.get(tripId);
        synchronized (ticketingMonitor) {
            Queue<String> ticketingQueue = new LinkedBlockingQueue<>();
            Queue<String> putResult = ticketingQueueMap.putIfAbsent(tripId, ticketingQueue);
            if (putResult != null) {
                ticketingQueue = putResult;
            }
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
        Queue<String> ticketingQueue = ticketingQueueMap.get(tripId);
        if (ticketingQueue == null || !ticketingQueue.remove(userId)) {
            throw new RuntimeException("当前用户未参与排队购票");
        }
    }

    @Override
    public Boolean getTicketingResult(String userId, String tripId) {
        Map<String, Boolean> ticketingResultMap = userTicketingResultMap.getOrDefault(userId, new HashMap<>());
        return ticketingResultMap.get(tripId);
    }
}