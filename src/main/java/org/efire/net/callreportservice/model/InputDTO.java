package org.efire.net.callreportservice.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by jongtenerife on 26/07/2017.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InputDTO {

    private LocalDate callDate;
    private LocalTime callTime;
    private Integer source;
    private Long destination;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;

}
