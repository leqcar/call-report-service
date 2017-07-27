package org.efire.net.callreportservice.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jongtenerife on 27/07/2017.
 */
public class DummyWriter implements ItemWriter<SourceCallLog> {

    private static final Logger LOG = LoggerFactory.getLogger(DummyWriter.class);

    @Override
    public void write(List<? extends SourceCallLog> list) throws Exception {
        List<LocalTime> durations = new ArrayList<>();
        for (SourceCallLog log :list
             ) {
            LOG.info(log.toString());
            durations.add(log.getDuration());
        }
        LocalTime initTime = LocalTime.of(00,00,00);
        int h=0;
        int m=0;
        int s=0;
        for (LocalTime d: durations
             ) {
            h = h + d.getHour();
            m = m + d.getMinute();
            s = s + d.getSecond();
        }

        LocalTime newTime  = initTime.plusHours(h).plusMinutes(m).plusSeconds(s);
        System.out.println("NEW TIME : "+newTime);
    }
}
