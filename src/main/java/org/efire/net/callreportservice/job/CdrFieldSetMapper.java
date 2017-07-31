package org.efire.net.callreportservice.job;

import org.efire.net.callreportservice.model.CallLog;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DELFIN.TENERIFE on 7/31/2017.
 */
public class CdrFieldSetMapper implements FieldSetMapper<CallLog> {
    //TODO: Use pattern regex
    //private static final String SECONDS_EXPR = "";
   // private static final Pattern secRegex = Pattern.compile(SECONDS_EXPR);

    @Override
    public CallLog mapFieldSet(FieldSet fieldSet) throws BindException {
        CallLog callLog = new CallLog();
        String[] dateTime = fieldSet.readString("Date").split(" ");
        callLog.setCallDate(parseDate(dateTime[0]));
        callLog.setCallTime(parseTime(dateTime[1]));
        callLog.setSource(fieldSet.readString("Source"));
        callLog.setDestination(fieldSet.readString("Destination"));
        callLog.setHours(Integer.parseInt("0"));
        callLog.setMinutes(Integer.parseInt("0"));
        callLog.setSeconds(parseSeconds(fieldSet.readString("Duration")));
        return callLog;
    }

    private Date parseDate(String dateAsString) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date dt = null;
        try {
            dt = sdf.parse(dateAsString);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return dt;
    }

    private LocalTime parseTime(String timeAsString) {
        String[] hhm = timeAsString.split(":");
        int hh = Integer.parseInt(hhm[0]);
        int mm = Integer.parseInt(hhm[1]);
        LocalTime time = LocalTime.of(hh, mm);
        return time;
    }

    private Integer parseSeconds(String timeAsString) {
        String[] s = timeAsString.split("s");
        return Integer.parseInt(s[0]);
    }
}


