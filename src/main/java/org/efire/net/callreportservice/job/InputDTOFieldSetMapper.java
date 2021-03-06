package org.efire.net.callreportservice.job;

import org.efire.net.callreportservice.model.CallLog;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by jongtenerife on 27/07/2017.
 */
@Component
public class InputDTOFieldSetMapper implements FieldSetMapper<CallLog> {

    @Override
    public CallLog mapFieldSet(FieldSet fieldSet) throws BindException{
        CallLog inputDTO = new CallLog();
        inputDTO.setCallDate(parseDate(fieldSet.readString("Date")));
        inputDTO.setCallTime(parseTime(fieldSet.readString("Time")));
        inputDTO.setSource(fieldSet.readString("Source"));
        inputDTO.setDestination(fieldSet.readString("Destination"));
        String[] hhmmss = fieldSet.readString("Duration").split(":");
        inputDTO.setHours(Integer.parseInt(hhmmss[0]));
        inputDTO.setMinutes(Integer.parseInt(hhmmss[1]));
        inputDTO.setSeconds(Integer.parseInt(hhmmss[2]));
        //inputDTO.setCallDuration(parseTime(fieldSet.readString("Duration")));

        return inputDTO;
    }

    private Date parseDate(String dateAsString) {
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        //LocalDate dt = LocalDate.parse(dateAsString, formatter);
        Date dt = null;
        try {
            dt = sdf.parse(dateAsString);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return dt;
    }

    private LocalTime parseTime(String timeAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime time = LocalTime.parse(timeAsString, formatter);
        return time;
    }
}
