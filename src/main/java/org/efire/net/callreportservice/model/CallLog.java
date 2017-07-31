package org.efire.net.callreportservice.model;

import lombok.*;

import java.time.LocalTime;
import java.util.Date;

/**
 * Created by jongtenerife on 26/07/2017.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CallLog {

    private Date callDate;
    private LocalTime callTime;
    private String source;
    private String destination;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;

}
