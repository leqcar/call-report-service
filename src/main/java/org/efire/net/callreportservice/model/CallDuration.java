package org.efire.net.callreportservice.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;

/**
 * Created by jongtenerife on 26/07/2017.
 */
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class CallDuration {

    private Long mobileNo;
    private LocalTime timeCall;
    private LocalTime duration;

}
