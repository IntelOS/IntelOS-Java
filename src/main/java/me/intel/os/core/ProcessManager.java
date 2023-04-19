package me.intel.os.core;

import me.intel.os.OS;
import me.intel.os.events.ProcessTimeoutEvent;

import java.util.concurrent.*;

public class ProcessManager {
    private int currID = 0;
    private static ProcessManager procManager;
    private Thread manager;
    private final ConcurrentLinkedQueue<Process> queuedProcess = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Integer, Process> runningProcesses = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    public void add(Process t) {
        queuedProcess.add(t);
    }

    public Thread getProcess(int id) {
        return runningProcesses.get(id).getThread();

    }
    @SuppressWarnings({"deprecation"})
    public void kill(int id) {
        runningProcesses.get(id).getThread().stop();
    }

    public void shutdown() {
        runningProcesses.forEach((k, v) -> { v.getThread().interrupt(); });
        runningProcesses.clear();
    }
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            if (!queuedProcess.isEmpty()) {
                Process process = queuedProcess.poll();
                Thread thread = process.start();
                if(thread != null) {
                    runningProcesses.put(currID, process);
                    process.setId(currID);
                    currID++;
                    // Watcher
                    if(!(process instanceof Service)) {
                        new Thread(() -> {
                            try {
                                thread.join(process.getTimeoutMillis());
                                if (thread.isAlive()) {
                                    thread.interrupt();
                                    OS.getEventHandler().post(new ProcessTimeoutEvent(thread, process.getId()));
                                    runningProcesses.remove(process.getId());
                                } else {
                                    runningProcesses.remove(process.getId());
                                }
                            }
                            catch (InterruptedException ignored) {}
                        }).start();
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    private ProcessManager() {}
    public static ProcessManager getProcessManager() {
        if(procManager == null) {
            procManager = new ProcessManager();
        }
        return procManager;
    }
}
