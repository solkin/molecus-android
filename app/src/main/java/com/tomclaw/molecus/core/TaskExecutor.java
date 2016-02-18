package com.tomclaw.molecus.core;

import org.androidannotations.annotations.EBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Solkin
 * Date: 31.10.13
 * Time: 10:56
 */
@EBean(scope = EBean.Scope.Singleton)
public class TaskExecutor {

    private final ExecutorService threadExecutor = Executors.newFixedThreadPool(5);

    public void execute(final Task task) {
        if (task.isPreExecuteRequired()) {
            MainExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    task.onPreExecuteMain();
                    threadExecutor.submit(task);
                }
            });
        } else {
            threadExecutor.submit(task);
        }
    }
}
