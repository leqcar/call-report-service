package org.efire.net.callreportservice.job;

import org.efire.net.callreportservice.model.CallReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Created by jongtenerife on 27/07/2017.
 */
@Component
public class CallLogProcessor implements ItemProcessor<CallReport, CallReport> {
    private static final Logger LOG = LoggerFactory.getLogger(CallLogProcessor.class);
    @Override
    public CallReport process(CallReport sourceCallLog) throws Exception {
        LocalTime initTime = LocalTime.of(00,00,00);
        sourceCallLog.setDuration(initTime.plusHours(sourceCallLog.getHours())
                .plusMinutes(sourceCallLog.getMinutes())
                .plusSeconds(sourceCallLog.getSeconds()));
        return sourceCallLog;
    }
}
