package org.efire.net.callreportservice.job;

import org.efire.net.callreportservice.model.InputDTO;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by jongtenerife on 27/07/2017.
 */
@Component
public class InputDTOFieldSetMapper implements FieldSetMapper<InputDTO> {

    @Override
    public InputDTO mapFieldSet(FieldSet fieldSet) throws BindException {
        InputDTO inputDTO = new InputDTO();
        inputDTO.setCallDate(parsetDate(fieldSet.readString("Date")));
        inputDTO.setCallTime(parseTime(fieldSet.readString("Time")));
        inputDTO.setSource(fieldSet.readInt("Source"));
        inputDTO.setDestination(fieldSet.readLong("Destination"));
        String[] hhmmss = fieldSet.readString("Duration").split(":");
        inputDTO.setHours(Integer.parseInt(hhmmss[0]));
        inputDTO.setMinutes(Integer.parseInt(hhmmss[1]));
        inputDTO.setSeconds(Integer.parseInt(hhmmss[2]));
        //inputDTO.setCallDuration(parseTime(fieldSet.readString("Duration")));

        return inputDTO;
    }

    private LocalDate parsetDate(String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyy");
        LocalDate date = LocalDate.parse(dateAsString, formatter);
        return date;
    }

    private LocalTime parseTime(String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime time = LocalTime.parse(dateAsString, formatter);
        return time;
    }
}
