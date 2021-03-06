package org.efire.net.callreportservice.job;

import org.efire.net.callreportservice.model.CallReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jongtenerife on 27/07/2017.
 */
public class DummyWriter implements ItemWriter<CallReport> {

    private static final Logger LOG = LoggerFactory.getLogger(DummyWriter.class);

    @Override
    public void write(List<? extends CallReport> list) throws Exception {
        List<LocalTime> durations = new ArrayList<>();
        for (CallReport log :list) {
            LocalTime initTime = LocalTime.of(00,00,00);
            LOG.info(String.format(" Source: %s", log.getSource()));
            LOG.debug(String.format("\tHours: %s", log.getHours()));
            LOG.debug(String.format("\tMinutes: %s", log.getMinutes()));
            LOG.debug(String.format("\tSeconds: %s", log.getSeconds()));
            String msg = "\tTotal duration of %s";
            LOG.info(String.format(msg,
                    initTime.plusHours(log.getHours())
                            .plusMinutes(log.getMinutes())
                            .plusSeconds(log.getSeconds())));
        }
    }
}
