package org.efire.net.callreportservice.job;

import lombok.*;

import java.time.LocalTime;

/**
 * Created by jongtenerife on 27/07/2017.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SourceCallLog {

    private Integer source;
    private LocalTime duration;

}
