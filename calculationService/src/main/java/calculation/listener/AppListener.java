package calculation.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import java.util.concurrent.ExecutorService;

public class AppListener implements ApplicationListener<ContextStoppedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AppListener.class);

    @Autowired
    private ExecutorService executorService;

    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {
        logger.info("contextStoppedEvent.");
        executorService.shutdown();
    }
}
