package org.efire.net.callreportservice.model;

import lombok.*;

import java.time.LocalTime;
import java.util.Date;

/**
 * Created by jongtenerife on 27/07/2017.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CallReport {
    private Date reportDate;
    private String source;
    private LocalTime duration;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;
}
