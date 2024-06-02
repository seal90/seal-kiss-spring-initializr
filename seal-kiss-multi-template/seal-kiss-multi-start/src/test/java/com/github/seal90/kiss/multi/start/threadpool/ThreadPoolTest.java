package com.github.seal90.kiss.multi.start.threadpool;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.*;

//@RunWith(MockitoJUnitRunner.class)
public class ThreadPoolTest {

    /**
     * get(3, TimeUnit.SECONDS);
     * get 3s 超时后，执行线程依旧执行完成了任务
     * 即打印出 打印结束
     */
//    @Test
    public void test1() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(50);
        threadPoolTaskExecutor.setMaxPoolSize(50);
        threadPoolTaskExecutor.setRejectedExecutionHandler((r,executor)-> {
            throw new RejectedExecutionException("Failed to start a new thread");
        });
        threadPoolTaskExecutor.setQueueCapacity(500);
        threadPoolTaskExecutor.setThreadNamePrefix("MINE-POOL-");
        threadPoolTaskExecutor.initialize();

        Future<String> future = threadPoolTaskExecutor.submit(()->{
            try {
                System.out.println("打印开始");
                Thread.sleep(10000L);
                System.out.println("打印结束");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "hello";
        });

        String val = null;
        try {
            val = future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        System.out.println(val);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
        }

    }

//    @Test
    public void test2() {
        ExecutorService executorService = new ThreadPoolExecutor(10,10, 30, TimeUnit.SECONDS,
                new LinkedBlockingDeque());

        Future<String> future = executorService.submit(()->{
            try {
                System.out.println("打印开始");
                Thread.sleep(10000L);
                System.out.println("打印结束");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "hello";
        });
        String val = null;
        try {
            val = future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        System.out.println(val);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
        }

    }

    /**
     * invokeAll(tasks, 2, TimeUnit.SECONDS);
     * 执行2s之内的成功获取到结果，超时的的到 InterruptedException，获取结果时 CancellationException
     */
//    @Test
    public void test3() {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // 创建任务列表
        List<Callable<String>> tasks = List.of(
                () -> {
                    try {
                        System.out.println("1开始执行");
                        Thread.sleep(1000); // 模拟耗时操作
                        System.out.println("1结束执行");
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("1Exception 结束");
                    }
                    return "Task 1 completed";
                },
                () -> {
                    try {
                        System.out.println("2开始执行");
                        Thread.sleep(3000); // 模拟耗时操作
                        System.out.println("2结束执行");
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("2Exception 结束");
                    }
                    return "Task 2 completed";
                },
                () -> {
                    try {
                        System.out.println("3开始执行");
                        Thread.sleep(1000); // 模拟耗时操作
                        System.out.println("3结束执行");
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("3Exception 结束");
                    }
                    return "Task 3 completed";
                }
        );

        try {
            // 执行任务并设置超时时间为2秒
            List<Future<String>> futures = executor.invokeAll(tasks, 2, TimeUnit.SECONDS);

            // 遍历任务结果
            for (Future<String> future : futures) {
                System.out.println("开始获取结果");
                try {
                    String result = future.get();
                    System.out.println(result);
                } catch (Exception e) {
                    System.out.println("获取结果异常");
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
//            e.printStackTrace();
        } finally {
            // 关闭线程池
//            executor.shutdown();
        }

        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
        }
    }
}
